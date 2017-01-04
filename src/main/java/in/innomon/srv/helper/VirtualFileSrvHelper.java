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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author ashish
 */
public class VirtualFileSrvHelper extends SysTimeHelper {

    private String fourOfourMsg = "<html><body><h1>404: File Not Found</h1></body></html>\n";
    private String fourOthreeMsg = "<html><body><h1>403: Forbidden content type</h1></body></html>\n";
    private String defaultIndexFile = "index.html";
   
    private String webContextPath = "/";
 
    private DbGetHelper vFileGetter = null;
    private DbGetHelper mimes = new Mimes();

    @Override
    public void handle(HttpExchange hx) throws IOException {
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
    
        
        // extract the sub path, index to the Coherence Cache
        String path = hx.getRequestURI().getPath();
        String subPath = path.substring(path.indexOf(webContextPath));

        //System.out.println(path.indexOf(webContextPath));

        if (subPath == null) {
            throw new IOException("Got Null Path from[" + path + "] context[" + webContextPath + "]");
        }
        if (subPath.endsWith("/")) {
            subPath = subPath + defaultIndexFile;
        }

        //System.out.println("subpath [" + subPath + "]");

        byte[] content = (byte[]) vFileGetter.get(subPath).getBytes(); // TODO: check safe conversion by instanceof
        int respCode = 200;
        String contentType = "text/html";
        if (content == null) {
            respCode = 404;
            content = (byte[]) vFileGetter.get("/"+respCode+".html").getBytes(); // TODO: check safe conversion by instanceof
            if (content == null) {
                content = fourOfourMsg.getBytes();
            }
        } else {
            contentType = getContentType(subPath);
            if (contentType == null) {
                respCode = 403;
                content = (byte[]) vFileGetter.get("/"+respCode+".html").getBytes();  // TODO: check safe conversion by instanceof
                if (content == null) {
                    content = fourOthreeMsg.getBytes();
                }
            }
        }
        Headers hdr = hx.getResponseHeaders();
        hdr.add("Content-type", contentType);
        hx.sendResponseHeaders(respCode, content.length);
        OutputStream out = hx.getResponseBody();
        out.write(content);
        out.close();
    }

    public DbGetHelper getVFileGetter() {
        return vFileGetter;
    }

    public void setVFileGetter(DbGetHelper vFileGetter) {
        this.vFileGetter = vFileGetter;
    }


    public String getContentType(String subPath) {
        String ret = null;
        int ndx = subPath.lastIndexOf('.');
        //System.out.println("getContent : subPath [" + subPath + "] ndx " + ndx);
        if (ndx >= 0) {
            String ext = subPath.substring(ndx);
            //System.out.println(" ext " + ext);
            ret = mimes.get(ext);
            //System.out.println(" ret " + ret);
        }
        return ret;
    }

 

    public String getFourOfourMsg() {
        return fourOfourMsg;
    }

    public void setFourOfourMsg(String fourOfourMsg) {
        this.fourOfourMsg = fourOfourMsg;
    }

    public String getFourOthreeMsg() {
        return fourOthreeMsg;
    }

    public void setFourOthreeMsg(String fourOthreeMsg) {
        this.fourOthreeMsg = fourOthreeMsg;
    }

    public String getDefaultIndexFile() {
        return defaultIndexFile;
    }

    public void setDefaultIndexFile(String defaultIndexFile) {
        this.defaultIndexFile = defaultIndexFile;
    }

 

    public String getWebContextPath() {
        return webContextPath;
    }

    public void setWebContextPath(String webContextPath) {
        this.webContextPath = webContextPath;
    }

  

    public DbGetHelper getMimes() {
        return mimes;
    }

    public void setMimes(DbGetHelper mimes) {
        this.mimes = mimes;
    }

}
