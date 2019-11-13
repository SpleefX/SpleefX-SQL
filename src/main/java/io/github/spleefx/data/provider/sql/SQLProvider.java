/*
 * * Copyright 2019 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.spleefx.data.provider.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.spleefx.SpleefX;
import io.github.spleefx.data.DataProvider;
import io.github.spleefx.data.GameStats;
import io.github.spleefx.data.PlayerStatistic;
import io.github.spleefx.extension.GameExtension;
import io.github.spleefx.util.io.FileManager;
import io.github.spleefx.util.plugin.PluginSettings;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.Connection;
import java.util.function.Supplier;

/**
 * Data provider for SQL
 */
public class SQLProvider implements DataProvider {

    /**
     * A quick accessor for the connection
     */
    protected static final Supplier<Connection> SQL = () -> ((SQLProvider) SpleefX.getPlugin().getDataProvider()).connection;

    /**
     * The SQL connection
     */
    private Connection connection;

    /**
     * Creates the required files for this provider
     *
     * @param fileManager File manager instance
     */
    @Override
    public void createRequiredFiles(FileManager<SpleefX> fileManager) {
        File file = new File(fileManager.getPlugin().getDataFolder(), PluginSettings.STATISTICS_DIRECTORY.get() + File.separator + PluginSettings.SQLITE_FILE_NAME.get());
        try {
            if (!file.exists()) { // Create the table
                file.createNewFile();
                connect();
                Query.createTable();
                return;
            }
            connect(); // The database already exists, so just connect
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns whether the player has an entry in the storage or not
     *
     * @param player Player to check for
     * @return {@code true} if the player is stored, false if otherwise.
     */
    @Override
    public boolean hasEntry(OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the player to the data entries
     *
     * @param player Player to add
     */
    @Override
    public void add(OfflinePlayer player) {
        Query.add(player, new GameStats());
    }

    /**
     * Retrieves the player's statistics from the specified extension
     *
     * @param statistic Statistic to retrieve
     * @param player    Player to retrieve from
     * @param extension The mode. Set to {@code null} to get global statistics
     * @return The statistic
     */
    @Override
    public int get(PlayerStatistic statistic, OfflinePlayer player, GameExtension extension) {
        return Query.get(player).get(statistic, extension);
    }

    /**
     * Adds the specified amount to the statistic
     *
     * @param statistic Statistic to add to
     * @param player    Player to add for
     * @param extension Mode to add for
     * @param addition  Value to add
     */
    @Override
    public void add(PlayerStatistic statistic, OfflinePlayer player, GameExtension extension, int addition) {
        Query.get(player).add(statistic, extension, addition);
    }

    /**
     * Saves all the entries of the data
     *
     * @param plugin Plugin instance
     */
    @Override
    public void saveEntries(SpleefX plugin) {
        Query.addAll();
    }

    /**
     * Returns the statistics of the specified player
     *
     * @param player Player to retrieve from
     * @return The player's statistics
     */
    @Override
    public GameStats getStatistics(OfflinePlayer player) {
        return Query.get(player);
    }

    /**
     * Connects to the database and sets the {@link Connection} instance
     */
    private void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:sqlite:" + new File(SpleefX.getPlugin().getDataFolder() + File.separator + PluginSettings.STATISTICS_DIRECTORY.get() +
                File.separator + PluginSettings.SQLITE_FILE_NAME.get()).getAbsolutePath();
        HikariConfig config = new HikariConfig();
        config.setPoolName("SpleefXPool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl(url);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 seconds
        config.setMaximumPoolSize(50); // 50 connections (including idle connections)
        HikariDataSource ds = new HikariDataSource(config);
        connection = ds.getConnection();
    }
}
