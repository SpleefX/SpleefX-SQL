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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.spleefx.data.DataException;
import io.github.spleefx.data.DataProvider;
import io.github.spleefx.data.GameStats;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An interface which contains standard SQL queries and methods for interacting with them
 */
public interface Query {

    /**
     * The cache map
     */
    Map<OfflinePlayer, GameStats> DATA_CACHE = new HashMap<>();

    /**
     * The GSON used to handle SQL responses
     */
    Gson SQL = new GsonBuilder().disableHtmlEscaping().create();

    /**
     * Query for creating the table.
     */
    String CREATE_TABLE = "create table Statistics (Player varchar not null constraint Statistics_pk primary key, Data varchar default '{}'); \n"
            + "create unique index Statistics_Player_uindex on Statistics (Player);\n";

    /**
     * Select query for getting data
     */
    String SELECT = "SELECT Player, Data FROM statistics WHERE Player = ?";

    /**
     * Query to update more than 1 entry all at once
     */
    String MULTI_UPDATE = "INSERT OR REPLACE INTO statistics(Player, Data) VALUES ";

    /**
     * A simple function for retrieving statistics for the specified player from the database
     * directly.
     */
    Function<OfflinePlayer, GameStats> REQUEST_FROM_DATABASE = (player) -> {
        try {
            PreparedStatement statement = SQLProvider.SQL.get().prepareStatement(SELECT);
            statement.setString(1, DataProvider.getStoringStrategy().apply(player));
            ResultSet set = statement.executeQuery();
            if (set.isClosed()) {
                GameStats stats = new GameStats();
                add(player, stats);
                return stats;
            }
            return SQL.fromJson(set.getString("Data"), GameStats.class);
        } catch (SQLException e) {
            throw new DataException(e);
        }
    };

    /**
     * Creates the SQL table
     */
    static void createTable() {
        try {
            PreparedStatement statement = SQLProvider.SQL.get().prepareStatement(CREATE_TABLE);
            statement.execute();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    /**
     * Adds the specified player to the database
     *
     * @param player     Player to add
     * @param statistics Statistics to add
     */
    static void add(OfflinePlayer player, GameStats statistics) {
        DATA_CACHE.put(player, statistics);
    }

    /**
     * Writes all the cache to the database
     */
    static void addAll() {
        if (DATA_CACHE.isEmpty()) return; // No cache to write
        try {
            /* Create the query */
            StringBuilder builder = new StringBuilder(MULTI_UPDATE);
            DATA_CACHE.forEach((player, statistic) -> builder
                    .append("('").append(DataProvider.getStoringStrategy().apply(player)).append("', '").append(Query.SQL.toJson(statistic)).append("'), "));
            String query = builder.toString().substring(0, builder.length() - 2) + ";";

            /* Execute the statement */
            PreparedStatement statement = SQLProvider.SQL.get().prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    /**
     * Retrieves the statistics of the specified player from the cache, or requests from
     * the database and caches the response
     *
     * @param player Player to retrieve for
     * @return The player's statistics
     */
    static GameStats get(OfflinePlayer player) {
        return DATA_CACHE.computeIfAbsent(player, REQUEST_FROM_DATABASE);
    }

}
