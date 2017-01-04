/*
* Copyright (c) 2016, BON BIZ IT Services Pvt LTD.
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both

* (a) the Software, and

* (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a “Larger Work” to which the Software is contributed by such licensors),
* 
* without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms.
* 
* This license is subject to the following condition:
* 
* The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* Author: Ashish Banerjee, ashish@bonbiz.in
*/
package in.innomon.srv.helper;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import com.sun.net.httpserver.Headers;
import java.io.OutputStream;

/**
 *
 * @author ashish
 */
public class JsonGetRec extends SysTimeHelper {

    protected DbGetHelper dbGethelper = null;
    protected String fourOfiveMsg = "{\"Error\": 405, \"Message\": \"Invalid Method. Only GET supported\"}";
    protected String fourOfourMsg = "{\"Error\": 404, \"Message\": \"Resource Not Found\"}";
    protected boolean trimContextPath = true;

    @Override
    public void handle(HttpExchange hx) throws IOException {
        if (dbGethelper == null) {
            throw new IOException("Undefined dbGetHelper");
        }
        String res = null;
        String method = hx.getRequestMethod();
        String reqUri = hx.getRequestURI().getPath();
        String baseCtx = getContextPath();
        int len = baseCtx.length();

        String resPath = reqUri.substring(len);
        if (trimContextPath) {
            int ndx = resPath.indexOf(getContextPath());
            if(ndx != -1)
                resPath = resPath.substring(ndx);
        }
        if (resPath.startsWith("/")) {
            resPath = resPath.substring(1);
        }

        Headers hdr = hx.getResponseHeaders();
        hdr.add("Content-type", "application/json");

        int resCode = 200;
        res = null;
        switch (method) {
            case "GET":
                if (dbGethelper != null) {
                    res = dbGethelper.get(resPath);
                }
                else {
                    res = "ERROR: DB Helper is null";
                    resCode = 500;
                }    
                break;
            default:
                res = fourOfiveMsg;
                resCode = 405;

        }

        if (res == null) {
            res =  "{\"Error\": 404, \"ContextPath\": \""+ resPath+"\", \"Message\": \"Resource Not Found\"}";                //fourOfourMsg;
            resCode = 404;
        }

        hx.sendResponseHeaders(resCode, 0);
        OutputStream out = hx.getResponseBody();
        out.write(res.getBytes());
        out.flush();
        out.close();

    }

    public String getFourOfiveMsg() {
        return fourOfiveMsg;
    }

    public boolean isTrimContextPath() {
        return trimContextPath;
    }

    public void setTrimContextPath(boolean trimContextPath) {
        this.trimContextPath = trimContextPath;
    }

    public void setFourOfiveMsg(String fourOfiveMsg) {
        this.fourOfiveMsg = fourOfiveMsg;
    }

    public String getFourOfourMsg() {
        return fourOfourMsg;
    }

    public void setFourOfourMsg(String fourOfourMsg) {
        this.fourOfourMsg = fourOfourMsg;
    }

    public DbGetHelper getDbGethelper() {
        return dbGethelper;
    }

    public void setDbGethelper(DbGetHelper gethelper) {
        this.dbGethelper = gethelper;
    }

}
