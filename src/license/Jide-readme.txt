The usage, dependency, redistributable status of each jar file under lib folder 

--- redistributable jars -----

    jide-common.jar
        the common jar used by all other JIDE products.
        This is redistributable if you purchased any of JIDE products.

    jide-action.jar
        the jar for JIDE Action Framework. It depends on jide-common.jar.
        This is redistributable if you purchased JIDE Action Framework product.

    jide-dock.jar
        the jar for JIDE Docking Framework. It depends on jide-common.jar.
        This is redistributable if you purchased JIDE Docking Framework product.

    jide-components.jar
        the jar for JIDE Components. It depends on jide-common.jar.
        This is redistributable if you purchased JIDE Components product.

    jide-grids.jar
        the jar for JIDE Grids. It depends on jide-common.jar.
        This is redistributable if you purchased JIDE Grids product.

    jide-dialogs.jar
        the jar for JIDE Dialogs. It depends on jide-common.jar.
        This is redistributable if you purchased JIDE Dialogs product.

    jide-shortcut.jar
        the jar for JIDE Shortcut Editor. It depends on jide-common.jar, jide-components.jar and jide-grids.jar.
        This is redistributable if you purchased JIDE Shortcut Editor product.

    jide-pivot.jar
        the jar for JIDE Pivot Grid. It depends on jide-common.jar and jide-grids.jar.
        This is redistributable if you purchased JIDE Pivot Grid product.

    jide-editor.jar
        the jar for JIDE Code Editor. It depends on jide-common.jar jide-grids.jar jide-components.jar and jide-shortcut.jar.
        This is redistributable if you purchased JIDE Code Editor product.

    jide-rss.jar
        the jar for JIDE Feed Reader. It depends on jide-common.jar jide-grids.jar jide-components.jar informa.jar hsqldb.jar and jdom.jar.
        This is redistributable if you purchased JIDE Feed Reader product.

    jide-dashboard.jar
        the jar for JIDE Dashboard. It depends on jide-common.jar and jide-components.jar.
        This is redistributable if you purchased JIDE Dashboard product.

    jide-data.jar
        the jar for JIDE Data Grids. It depends on jide-common.jar, jide-grids.jar and jide-action.jar (only for
        CommandBar as PageNavigationBar extends it).
        This is redistributable if you purchased JIDE Data Grids product.

    jide-charts.jar
        the jar for JIDE Charts. It depends on jide-common.jar, commons-math-1.2.jar, servlet-api.jar and batik-awt-util.jar.
        This is redistributable if you purchased JIDE Charts product.

    jide-gantt.jar
        the jar for JIDE Gantt Chart. It depends on jide-common.jar and jide-grids.jar.
        This is redistributable if you purchased JIDE Gantt Chart product.

    jide-diff.jar
        the jar for JIDE Diff. It depends on jide-common.jar, and jide-editor.jar.
        This is redistributable if you purchased JIDE Diff product.

    jide-plaf.jar or jide-plaf-jdk7.jar
        the jar contains a few extra classes to support some additional L&Fs (such as Synthetica, Plastic, Tonic, A03, GTK).
        If you use those L&Fs in your application, you should include this jar in your class path. Otherwise no need to include.
        If you are using JDK7, please use jide-plaf-jdk7.jar instead which provides better support for Synth-based L&F such as GTK.
        However the support for Nimbus, although it is also Synth-based, is not included at the moment.

    jide-properties.jar
        the jar contains all the properties files (localized strings) for different languages. If your application
        will run on different locales, you should include this jar file in the class path. Please note, this jar file
        contains as many as 48 languages for some of the properties file. So feel free to edit this jar to remove
        those properties files that your application won't need to reduce the jar file size.

-- for JIDE Grids and JIDE Pivot Grid only and only if you use export to Excel feature --
    poi-3.8-20120326.jar
        Download URL: http://poi.apache.org/download.html
         Description: Apache license, version 3.8-20120326
                      a 3rd party jar used by JIDE Grids and JIDE Pivot Grid. This is only used to implement Excel(TM) export feature.
          Dependency: it depends on following libraries. JIDE has no direct dependency on these two jars.
                      poi-ooxml-schemas-3.8-20120326.jar, If you don't export format of xlsx, you don't need to include this jar.
                      xmlbeans-2.3.0.jar, If you don't use HssfTableUtils and HssfPivotTableUtils, you don't need to include this jar.
                      Refer to http://poi.apache.org/overview.html#components for more details.

    lucene-core-4.10.2.jar
    lucene-queryparser-4.10.2.jar
    lucene-analyzers-common-4.10.2.jar
        Download URL: http://lucene.apache.org/java/docs/index.html
         Description: Apache license, version 4.10.2
                      a 3rd party jar used by JIDE Grids. This is only used in LuceneFilterableTableModel and
                      LuceneQuickTableFilterField. If you don't use these two classes, you don't need to include this jar.

-- For JIDE Feed Reader only --
    informa.jar
        Download URL: http://informa.sourceforge.net/
         Description: LGPL, version 0.7.0-alpha2
                      3rd party jars used by JIDE Feed Reader. Informa project is a news aggregation library based on the Java Platform.
          Dependency: It depends on commons-logging.jar and jdom.jar. JIDE has no direct dependency on these two jars

    commons-logging.jar
        Download URL: http://commons.apache.org/logging/
         Description: Apache license, version 1.0.4

    jdom.jar
        Download URL: http://www.jdom.org/
         Description: Apache license, version 1.0

    hsqldb.jar
        Download URL: http://hsqldb.org/
         Description: BSD license, version 2.3.2
                      a 3rd party jar used by JIDE Feed Reader. It is a "Lightweight 100% Java SQL Database Engine".
          Dependency: It's dependency is optional if you provide your own PersistenceManagerIF other than the default
                      FeedDatabasePersistenceManager.

-- For JIDE Data Grids only and only if you use HibernateTableModel and HibernatePageTableModel --
    hibernate-release-4.3.7.Final.jar
        Download URL: http://hibernate.org
         Description: LGPL, version 4.3.7 final
                      3rd party jars used by JIDE Data Grids. You may use all the jars in the hibernate release instead of taking them
                      from here. If you don't use any package under com.jidesoft.hibernate, you don't need this jar.
          Dependency: Hibernate 4 depends on the following libraries. JIDE has no direct dependency on these jars.
                      antlr-2.7.7.jar
                      dom4j-1.6.1.jar
                      hibernate-commons-annotations-4.0.5.Final.jar
                      hibernate-core-4.3.7.Final.jar
                      hibernate-jpa-2.1-api-1.0.0.Final.jar
                      jboss-logging-3.1.3.GA.jar
                      jboss-transaction-api_1.2_spec-1.0.0.Final.jar

-- For JIDE Charts only --
    batik-awt-util.jar
        Download URL: http://xmlgraphics.apache.org/batik/license.html
         Description: Apache license, version 2.0
                      3rd party jar used by JIDE Charts. If you are using JDK6 and above, you do not need this jar.

    commons-math-2.2.jar
        Download URL: http://commons.apache.org/math/
         Description: Apache license, version 2.2
                      3rd party jar used by JIDE Charts. It is only used by classes from com.jidesoft.chart.fit package.

    servlet-api.jar
         Description: part of Java Servlet API.
                      It is only used by classes from com.jidesoft.chart.servlet package.

--- non-redistributable jars -----

    miglayout-xxx.jar
        a 3rd party jar used in JIDE Dashboard PredefinedLayoutDashboardDemo. It is used only in this demo and jide-dashboard.jar
        doesn't use it at all.
        This jar is NOT redistributable. Do not include it in your application's class path.

    derby.jar
        a 3rd party jar used in the demo of JIDE Data Grids. It is used only in the demo.

    jide-designer.jar
        the jar for Visual Designer. It depends on jide-common.jar, jide-dock.jar, jide-action.jar, jide-dialogs.jar,
        jide-components.jar, jide-grids.jar, xerces.jar, velocity-dep-1.4.jar.
        This jar is NOT redistributable. Do not include it in your application's class path.

    jide-beaninfo.jar
        the jar for any 3rd party GUI Builders. It depends on jide-common.jar, jide-dock.jar, jide-action.jar, jide-dialogs.jar,
        jide-components.jar, jide-grids.jar.
        This jar is NOT redistributable. Do not include it in your application's class path.

    velocity-dep-1.4.jar
        a 3rd party jar used by Visual Designer.
        This jar is NOT redistributable. Do not include it in your application's class path.
        If you need to use velocity for your own application, please get it from http://jakarta.apache.org/velocity/.

    xerces.jar
        a 3rd party jar used by Visual Designer.
        This jar is NOT redistributable. Do not include it in your application's class path.
        If you need to use xerces for your own application, please get it from http://xml.apache.org/.

