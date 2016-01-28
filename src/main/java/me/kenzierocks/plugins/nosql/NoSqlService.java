/*
 * This file is part of NoSqlService, licensed under the MIT License (MIT).
 *
 * Copyright (c) kenzierocks (Kenzie Togami) <https://kenzierocks.me>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.kenzierocks.plugins.nosql;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.sql.SqlService;

import com.google.common.base.Throwables;
import com.google.inject.Inject;

@Plugin(id = NoSqlService.ID, name = NoSqlService.NAME,
        version = NoSqlService.VERSION)
public final class NoSqlService {

    public static final String ID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VERSION@";

    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path cfgDir;

    public Logger getLogger() {
        return this.logger;
    }

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        this.logger.info("Loading " + NAME + " v" + VERSION);
        SqlService sql = Sponge.getServiceManager().provide(SqlService.class)
                .orElseThrow(() -> new IllegalStateException("NoSQL?"));
        this.logger.info("Yay, SQL exists! " + sql);
        try {
            Files.createDirectories(this.cfgDir);
            DataSource dataSource = sql
                    .getDataSource("jdbc:h2:" + this.cfgDir.resolve("test.h2"));
            Connection connection = dataSource.getConnection();
            connection.createStatement().execute("CREATE TABLE TEST");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        this.logger.info("Loaded " + NAME + " v" + VERSION);
    }

}
