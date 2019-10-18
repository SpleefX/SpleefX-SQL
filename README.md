# SpleefX-SQL
SpleefX-SQL is a simple JAR to allow access to SQL features in SpleefX. This was mainly separated from the main JAR due to the big size of the required SQLite frameworks (SQLite drivers, MySQL provider), in addition to the optimizing frameworks (HikariCP).

Do note that the addon **does not function on its own**. For it to have an effect, set the **StorageType** in **PlayerGameStatistics** to `SQLITE`, and the plugin will look for SpleefX-SQL to handle all SQL code.

# For server admins
If you would like to use the SQLite feature in SpleefX to store data, simply follow the instructions below:

**1.** Download [SpleefX-SQL v1.0-SNAPSHOT](https://github.com/SpleefX/SpleefX-SQL/releases/download/1.0-SNAPSHOT/SpleefXSQL-1.0-SNAPSHOT.jar).

**2.** Add the downloaded JAR file to your **/plugins/** directory.

**3.** Restart the server for the addon to take action.