/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.mas.stream.buffer

import akka.NotUsed
import akka.stream.scaladsl.Flow
import net.ceedubs.ficus.Ficus._
import pl.edu.agh.scalamas.app.{AgentRuntimeComponent, ConcurrentAgentRuntimeComponent}
import pl.edu.agh.scalamas.app.stream.graphs.{AnnealedShufflingBufferFlow, BarrierBufferFlow, ShufflingBufferFlow}
import pl.edu.agh.scalamas.mas.LogicTypes.Agent
import pl.edu.agh.scalamas.random.RandomGeneratorComponent

import scala.util.Try

trait AgentBufferStrategy {

  protected def agentBufferFlow: Flow[Agent, Agent, NotUsed]

}

trait ShufflingBufferStrategy
  extends AgentBufferStrategy { this: AgentRuntimeComponent with RandomGeneratorComponent =>

  private lazy val size = agentRuntime.config.as[Int]("streaming.continuous.shuffling-buffer-size")

  protected def agentBufferFlow: Flow[Agent, Agent, NotUsed] = {
    ShufflingBufferFlow[Agent](size)(randomData)
  }
}

trait AnnealedShufflingStrategy
  extends AgentBufferStrategy { this: AgentRuntimeComponent with RandomGeneratorComponent =>

  protected def agentOrdering: Ordering[Agent]

  private lazy val size = agentRuntime.config.as[Int]("streaming.continuous.shuffling-buffer-size")
  private lazy val halfDecayInSeconds = {
    val decay = agentRuntime.config.as[String]("streaming.continuous.halfDecayInSeconds")
    Try(decay.toInt).toOption
  }

  protected def agentBufferFlow: Flow[Agent, Agent, NotUsed] = {
    AnnealedShufflingBufferFlow[Agent](
      size, halfDecayInSeconds
    )(randomData, agentOrdering)
  }
}

trait BarrierBufferStrategy extends AgentBufferStrategy {
  this: RandomGeneratorComponent =>

  protected def expectedTotal: Int

  protected def weight(a: Agent): Int

  protected def agentBufferFlow: Flow[Agent, Agent, NotUsed] = {
    BarrierBufferFlow.withAcc[Int, Agent](0) {
      case (currentTotal, agent) =>
        val newTotal = currentTotal + weight(agent)
        if (newTotal >= expectedTotal) {
          0 -> true
        } else {
          newTotal -> false
        }
    }(randomData)
  }
}