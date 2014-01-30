package net.audumla.akka;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import akka.actor.AbstractActor;
import akka.io.Tcp;
import akka.japi.Creator;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class CommandMessageTarget<T, M extends CommandEvent<T>> extends AbstractActor {
    private static final Logger logger = LoggerFactory.getLogger(CommandMessageTarget.class);


    public CommandMessageTarget(T reference) {
        this.reference = reference;
    }

    protected T reference;

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.
                match(CommandEvent.class, new FI.UnitApply<CommandEvent>() {
                    @Override
                    public void apply(CommandEvent c) {
                        try {
                            logger.debug("["+self().path().name()+"] received command ["+c.getClass()+"]");
                            c.execute(reference);
                        } catch (Throwable throwable) {
                            unhandled(c);
                        }
                    }
                }).
                build();
    }
}
