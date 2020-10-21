/*
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

val openosrsVersion = "3.4.4"

plugins {
    java
}

group = "com.sythelib.plugins.sythelibapi"
version = "1.0.2"

project.extra["PluginProvider"] = "sythelib"
project.extra["ProjectUrl"] = "https://gitlab.com/Gabulhas/sythelib-api"
project.extra["PluginLicense"] = "BSD 2-Clause License"
project.extra["PluginName"] = "sythelib-api"
project.extra["PluginDescription"] = "OpenOSRS API for SytheLib"

repositories {
    jcenter {
        content {
            excludeGroupByRegex("com\\.openosrs.*")
            excludeGroupByRegex("com\\.runelite.*")
        }
    }

    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://repo.runelite.net")
            }
        }
        filter {
            includeModule("net.runelite", "discord")
            includeModule("net.runelite.jogl", "jogl-all")
            includeModule("net.runelite.gluegen", "gluegen-rt")
        }
    }

    exclusiveContent {
        forRepository {
            mavenLocal()
        }
        filter {
            includeGroupByRegex("com\\.openosrs.*")
        }
    }
    maven("https://dl.bintray.com/oprs/")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.12")
    annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.3.1")

    compileOnly(group = "com.openosrs", name = "http-api", version = openosrsVersion)
    compileOnly(group = "com.openosrs", name = "runelite-api", version = openosrsVersion)
    compileOnly(group = "com.openosrs", name = "runelite-client", version = openosrsVersion)
    compileOnly(group = "com.openosrs.rs", name = "runescape-api", version = openosrsVersion)

    // client dependencies
    compileOnly(group = "org.apache.commons", name = "commons-text", version = "1.8")
    compileOnly(group = "com.google.guava", name = "guava", version = "29.0-jre")
    compileOnly(group = "com.google.inject", name = "guice", version = "4.2.3", classifier = "no_aop")
    compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
    compileOnly(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
    compileOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.12")
    compileOnly(group = "com.squareup.okhttp3", name = "okhttp", version = "4.8.0")
    compileOnly(group = "org.pf4j", name = "pf4j", version = "3.3.1")
    compileOnly(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.0.4")
    compileOnly(group = "org.pushing-pixels", name = "radiance-substance", version = "2.5.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Jar> {
        doLast {
            copy {
                from("./build/libs/")
                into(System.getProperty("user.home") + "/.runelite/externalmanager")
            }
        }
    }

    jar {
        manifest {
            attributes(mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to (project.extra["PluginName"] as String + "-plugin"),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}