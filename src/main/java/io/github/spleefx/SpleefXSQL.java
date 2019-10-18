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
package io.github.spleefx;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * SpleefXSQL: A separate JAR for handling SQL code. This was mainly separated due to the big size of SQLite
 * libraries, and in an attempt to avoid increasing the JAR size for a feature that not many would use.
 */
public final class SpleefXSQL extends JavaPlugin {
}
