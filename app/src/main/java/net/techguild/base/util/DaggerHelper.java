package net.techguild.base.util;

import net.techguild.base.CApp;
import net.techguild.base.data.module.BasicModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Allows easy swapping of production and test modules to satisfy Dagger dependencies
 */
public class DaggerHelper {
    //DAGGER
    private static ObjectGraph sObjectGraph;

    /**
     * Init the dagger object graph with production modules
     */
    public synchronized static void initProductionModules(CApp app) {
        initWithModules(getProductionModules(app));
    }

    /**
     * If passing in test modules make sure to override = true in the @Module annotation
     */
    public synchronized static void initWithTestModules(CApp app, Object... testModules) {
        initWithModules(getModulesAsList(app, testModules));
    }

    /**
     * Will create a new object graph and therefore reset any previous modules set * * @param modules
     */
    private synchronized static void initWithModules(List<Object> modules) {
        sObjectGraph = ObjectGraph.create(modules.toArray());
    }

    private synchronized static List<Object> getModulesAsList(CApp app, Object... extraModules) {
        List<Object> allModules = new ArrayList<Object>();
        allModules.addAll(getProductionModules(app));
        allModules.addAll(Arrays.asList(extraModules));
        return allModules;
    }

    /**
     * Dagger convience method - will inject the fields of the passed in object
     */
    public synchronized static void inject(Object object) {
        sObjectGraph.inject(object);
    }

    private synchronized static List<Object> getProductionModules(CApp app) {
        List<Object> productionModules = new ArrayList<Object>();
        productionModules.add(new BasicModule(app));
        return productionModules;
    }
}
