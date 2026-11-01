package com.glamoraa.listener;

import com.glamoraa.service.AuthenticationService;
import com.glamoraa.util.Db;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Startup checks: database reachable? first admin needed? Shutdown: release JDBC drivers. */
@WebListener
public class AppInitListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppInitListener.class.getName());

    @Override public void contextInitialized(ServletContextEvent sce) {
        try (Connection c = Db.get()) {
            LOG.info("Glamoraa connected to database: " + c.getMetaData().getURL().replaceAll("\\?.*", ""));
            new AuthenticationService().bootstrapAdmin();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Glamoraa could not reach the database. Check db.url / db.user / db.password and that glamoraa_db exists.", e);
        }
    }

    @Override public void contextDestroyed(ServletContextEvent sce) {
        ClassLoader mine = getClass().getClassLoader();
        for (Driver d : Collections.list(DriverManager.getDrivers())) {
            if (d.getClass().getClassLoader() == mine) {
                try { DriverManager.deregisterDriver(d); } catch (Exception e) { LOG.log(Level.WARNING, "Driver deregistration failed", e); }
            }
        }
    }
}
