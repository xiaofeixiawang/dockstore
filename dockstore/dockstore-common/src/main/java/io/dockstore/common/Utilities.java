/*
 *    Copyright 2016 OICR
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.dockstore.common;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author xliu
 */
public class Utilities {

    private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);


    public static HierarchicalINIConfiguration parseConfig(String path) {
        try {
            return new HierarchicalINIConfiguration(path);
        } catch (ConfigurationException ex) {
            throw new RuntimeException("Could not read ~/.dockstore/config");
        }
    }

    public static ImmutablePair<String, String> executeCommand(String command) {
        return executeCommand(command, true, Optional.of(ByteStreams.nullOutputStream()) , Optional.of(ByteStreams.nullOutputStream()));
    }

    public static ImmutablePair<String, String> executeCommand(String command, OutputStream stdoutStream, OutputStream stderrStream) {
        return executeCommand(command, true, Optional.of(stdoutStream), Optional.of(stderrStream));
    }

    /**
     * Execute a command and return stdout and stderr
     * @param command the command to execute
     * @return the stdout and stderr
     */
    private static ImmutablePair<String, String> executeCommand(String command, final boolean dumpOutput,
            Optional<OutputStream> stdoutStream, Optional<OutputStream> stderrStream) {
        // TODO: limit our output in case the called program goes crazy

        // these are for returning the output for use by this
        try (ByteArrayOutputStream localStdoutStream = new ByteArrayOutputStream();
                ByteArrayOutputStream localStdErrStream = new ByteArrayOutputStream()
        ) {
            OutputStream stdout = localStdoutStream;
            OutputStream stderr = localStdErrStream;
            if (stdoutStream.isPresent()) {
                assert stderrStream.isPresent();
                // in this branch, we want a copy of the output for Consonance
                stdout = new TeeOutputStream(localStdoutStream, stdoutStream.get());
                stderr = new TeeOutputStream(localStdErrStream, stderrStream.get());
            }

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            String utf8 = StandardCharsets.UTF_8.name();
            try {
                final CommandLine parse = CommandLine.parse(command);
                Executor executor = new DefaultExecutor();
                executor.setExitValue(0);
                if (dumpOutput) {
                    LOG.info("CMD: " + command);
                }
                // get stdout and stderr
                executor.setStreamHandler(new PumpStreamHandler(stdout, stderr));
                executor.execute(parse, resultHandler);
                resultHandler.waitFor();
                // not sure why commons-exec does not throw an exception
                if (resultHandler.getExitValue() != 0) {
                    resultHandler.getException().printStackTrace();
                    throw new ExecuteException("problems running command: " + command, resultHandler.getExitValue());
                }
                return new ImmutablePair<>(localStdoutStream.toString(utf8), localStdErrStream.toString(utf8));
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException("problems running command: " + command, e);
            } finally {
                if (dumpOutput) {
                    LOG.info("exit code: " + resultHandler.getExitValue());
                    try {
                        LOG.debug("stderr was: " + localStdErrStream.toString(utf8));
                        LOG.debug("stdout was: " + localStdoutStream.toString(utf8));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("utf-8 does not exist?", e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("could not close output streams", e);
        }
    }
}
