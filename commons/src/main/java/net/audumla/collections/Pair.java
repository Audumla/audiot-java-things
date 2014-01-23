package net.audumla.collections;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pair<Item1, Item2> {
    private static final Logger logger = LoggerFactory.getLogger(Pair.class);

    protected Item1 item1;
    protected Item2 item2;

    public Pair(Item1 item1, Item2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public Item1 getItem1() {
        return item1;
    }

    public void setItem1(Item1 item1) {
        this.item1 = item1;
    }

    public Item2 getItem2() {
        return item2;
    }

    public void setItem2(Item2 item2) {
        this.item2 = item2;
    }
}
