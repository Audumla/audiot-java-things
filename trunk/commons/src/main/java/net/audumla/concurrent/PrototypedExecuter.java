package net.audumla.concurrent;

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

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class PrototypedExecuter<T> extends InLineExecuter {
    private static final Logger logger = LoggerFactory.getLogger(PrototypedExecuter.class);

    private T protoType;

    public PrototypedExecuter() {
    }

    public PrototypedExecuter(T protoType) {
        this.protoType = protoType;
    }

    public T getProtoType() {
        return protoType;
    }

    public void setProtoType(T protoType) {
        this.protoType = protoType;
    }


    @Override
    public void execute(Runnable command) {
        try {
            BeanUtils.copyProperties(command,protoType);
        } catch (IllegalAccessException|InvocationTargetException e) {
            logger.error("Cannot copy prototype",command);
        }
        super.execute(command);
    }

    public Object executeObject(Callable command) throws Exception {
        BeanUtils.copyProperties(command,protoType);
        return super.execute((Callable<?>)command);
    }
}
