/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 * 
 * Portions Copyrighted 2011 Ashish Banerjee, @innomon
 * Ashish Banerjee elects to include this software in this distribution under the  GPL Version 2 license.
 */
package in.innomon.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ashish
 */
public class EventCentral implements Runnable {
    private static ArrayList<EventListner> listners = new ArrayList<EventListner>();

     static int maxEventThreads = 4; // visible within the package only for EventCentralConfig use,
     private static ExecutorService execServ = null;
    
    private Event event;
    private EventCentral(Event event) {
       this.event = event;
    }
    
    public static void publish(Event evt) {
        if(!listners.isEmpty()) {
            if(execServ == null) {
                synchronized(EventCentral.class) {
                execServ = Executors.newFixedThreadPool(maxEventThreads);
                }
            }    
            execServ.submit(new EventCentral(evt));
        }
    }
    public static void subscribe(EventListner listner) {
        listners.add(listner);
    }
    // TODO: Fix concorrent exception issues, trying to remove when some events are still pending execution.
    public static void unsubscribe(EventListner listner) {
        listners.remove(listner);
    }
    @Override
    public void run() {
        Iterator<EventListner> iter = listners.iterator();
        while(iter.hasNext()) {
            EventListner listner = iter.next();
            if(listner.isEventOfInterest(event))
                listner.onEvent(event);
        }
    }
}
