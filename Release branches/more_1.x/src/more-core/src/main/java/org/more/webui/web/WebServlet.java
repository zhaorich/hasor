/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.webui.web;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Lifecycle;
/**
 * 
 * @version : 2012-7-31
 * @author ������ (zyc@byshell.org)
 */
public class WebServlet extends HttpServlet {
    private static final long serialVersionUID = 9189308591013074938L;
    private Lifecycle         lifecycle        = null;
    private FacesConfig       config           = null;
    private FacesContext      uiContext        = null;
    /*-----------------------------------------------------------------------------------*/
    @Override
    protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse res = (HttpServletResponse) arg1;
        if (req.getRequestURI().endsWith(this.config.getFacesSuffix()) == true) {
            ViewContext viewContext = new ViewContext(req, res, this.uiContext);
            ViewContext.setCurrentViewContext(viewContext);
            this.lifecycle.execute(viewContext);
            ViewContext.setCurrentViewContext(null);
        }
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.config = new FacesConfig(new TempFilterConfig(config));
            String factoryClass = this.config.getWebUIFactoryClass();
            Class<?> factory = Thread.currentThread().getContextClassLoader().loadClass(factoryClass);
            WebUIFactory webuiFactory = (WebUIFactory) factory.newInstance();
            FactoryBuild build = new FactoryBuild(webuiFactory);
            this.uiContext = build.buildFacesContext(this.config);
            this.lifecycle = build.buildLifestyle(this.config, this.uiContext);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    @Override
    public void destroy() {}
}
class TempFilterConfig implements FilterConfig {
    private ServletConfig config = null;
    public TempFilterConfig(ServletConfig config) {
        this.config = config;
    }
    @Override
    public String getFilterName() {
        return config.getServletName();
    }
    @Override
    public String getInitParameter(String name) {
        return config.getInitParameter(name);
    }
    @Override
    public Enumeration getInitParameterNames() {
        return config.getInitParameterNames();
    }
    @Override
    public ServletContext getServletContext() {
        return config.getServletContext();
    }
}