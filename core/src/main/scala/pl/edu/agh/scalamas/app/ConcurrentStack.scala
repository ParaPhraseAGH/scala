/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.scalamas.app

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.scalamas.mas.EnvironmentStrategy
import org.scalamas.random.ConcurrentRandomGenerator
import org.scalamas.stats.{ConcurrentStatsFactory, StatsComponent}

/**
 * Created by Daniel on 2015-01-14.
 */
class ConcurrentStack(name: String)
  extends ConcurrentAgentRuntimeComponent
  with ConcurrentStatsFactory
  with ConcurrentRandomGenerator
  with ConcurrentRunner {

  this: EnvironmentStrategy with StatsComponent =>

  val agentRuntime = new ConcurrentAgentRuntime {

    val config: Config = ConfigFactory.load()

    val system: ActorSystem = ActorSystem(name)
  }

}