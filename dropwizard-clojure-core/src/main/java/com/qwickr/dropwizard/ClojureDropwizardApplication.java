package com.qwickr.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Map;
import java.util.List;
import io.dropwizard.jersey.DropwizardResourceConfig;
import org.eclipse.jetty.server.Server;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;


public abstract class ClojureDropwizardApplication extends Application<ClojureDropwizardConfiguration> {

	private Server jettyServer;

	private Environment environment;

    private Container jettyContainer;

	@Override
    public void run(ClojureDropwizardConfiguration configuration, Environment environment) {
        runWithSettings(configuration.getSettings() , environment);
        environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
    		@Override
    		public void serverStarted(Server server) {
        		jettyServer = server;
    		}
		});

        environment.jersey().register(new AbstractContainerLifecycleListener()  {
            @Override
            public void onStartup(Container container) {
                System.out.println("setting the container");
                jettyContainer = container;
            }
        });  


		this.environment = environment;
    }

    public abstract void runWithSettings(Map settings, Environment environment);

    public final void stop() throws Exception{
    	jettyServer.stop();
    }

    public final Environment getEnvironment(){
    	return this.environment;
    }

    public final Container getContainer(){
        return this.jettyContainer;
    }

    public synchronized void reload(List<Object> resources) {
        DropwizardResourceConfig dropwizardResourceConfig = new DropwizardResourceConfig(environment.metrics());
        for(Object o : resources){
            dropwizardResourceConfig.register(o);
        }
        this.jettyContainer.reload(dropwizardResourceConfig);
    }
}