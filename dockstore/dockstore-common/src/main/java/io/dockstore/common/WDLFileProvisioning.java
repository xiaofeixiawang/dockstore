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

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class deals with file provisioning for WDL
 * Created by aduncan on 10/03/16.
 */
public class WDLFileProvisioning {
        private static final Logger LOG = LoggerFactory.getLogger(WDLFileProvisioning.class);

        private final FileProvisioning fileProvisioning;

        public WDLFileProvisioning(String configFile) {
                fileProvisioning = new FileProvisioning(configFile);
        }

        /**
         * Pulls remote files from S3, DCC or HTTP and stores them locally.
         * A map is created to replace the input file entries in the input JSON file, where remote paths will be changed to local paths.
         * @param inputFilesJson Map of all input files from the input JSON file, key = fully qualified name(fqn), value = type (ex. file)
         * @param originalInputJson Map of input JSON file
         * @return A new mapping of fully qualified name to input file string or list of input file strings
         */
        public Map<String, Object> pullFiles(Map<String, Object> inputFilesJson, Map<String, String> originalInputJson) {
                // Download remote files into specific local locations
                Map<String, Object> fileMap = new HashMap<>();

                LOG.info("DOWNLOADING INPUT FILES...");

                // Go through input file fully qualified names
                for (Map.Entry<String, String> originalInputJsonEntry : originalInputJson.entrySet()) {
                        LOG.info(originalInputJsonEntry.getKey());
                        // Find matching name value in JSON parameter file
                        for (Map.Entry<String, Object> stringObjectEntry : inputFilesJson.entrySet()) {
                                // If the entry matches
                                if (stringObjectEntry.getKey().equals(originalInputJsonEntry.getKey())) {
                                        // Check if File or Array of Files
                                        if (stringObjectEntry.getValue() instanceof ArrayList) {
                                                // Iterate through object
                                                List stringObjectEntryList = (List)stringObjectEntry.getValue();
                                                ArrayList<String> updatedPaths = new ArrayList<>();
                                                for (Object entry : stringObjectEntryList) {
                                                        if (entry instanceof String) {
                                                                updatedPaths.add(doProcessFile(stringObjectEntry.getKey(), entry.toString()).get(stringObjectEntry.getKey()).toString());
                                                        }
                                                }

                                                fileMap.put(stringObjectEntry.getKey(), updatedPaths);

                                        } else if (stringObjectEntry.getValue() instanceof String) {
                                                // Just a file
                                                Map<String, Object> tempMap;
                                                tempMap = doProcessFile(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
                                                fileMap.putAll(tempMap);
                                        }
                                }
                        }
                }

                return fileMap;
        }

        /**
         * Create a mapping of the input files, including newly localized files
         * @param key Fully Qualified Name
         * @param path Original Path
         * @return Mapping of fully qualified name to new input file string or list of new input file strings
         */
        public Map<String, Object> doProcessFile(String key, String path) {
                FileProvisioning.PathInfo pathInfo = new FileProvisioning.PathInfo(path);
                Map<String, Object> jsonEntry = new HashMap<>();

                if (!pathInfo.isLocalFileType()) {

                        LOG.info("PATH TO DOWNLOAD FROM: {} FOR {}", path, key);

                        // Setup local paths
                        String downloadDir = "cromwell-input/" + UUID.randomUUID();
                        Utilities.executeCommand("mkdir -p " + downloadDir);
                        File downloadDirFileObject = new File(downloadDir);
                        String targetFilePath = downloadDirFileObject.getAbsolutePath() + "/" + key;

                        fileProvisioning.provisionInputFile(path, downloadDir, downloadDirFileObject, targetFilePath, pathInfo);

                        jsonEntry.put(key, targetFilePath);
                        LOG.info("DOWNLOADED FILE: LOCAL: {} URL: {} => {}", key, path, targetFilePath);
                } else {
                        jsonEntry.put(key, path);
                }
                return jsonEntry;

        }

        /**
         * Creates a new mapping to represent the Input JSON file with updated file paths
         * @param originalInputJson
         * @param newInputJson
         * @return Path to new JSON file
         */
        public String createUpdatedInputsJson(Map<String, Object> originalInputJson, Map<String, Object> newInputJson) {
                JSONObject newJSON = new JSONObject();
                for (String paramName : originalInputJson.keySet()) {
                        boolean isNew = false; // is the entry from the newInputJson mapping?

                        // Get value of mapping
                        final Object currentParam = originalInputJson.get(paramName);

                        // Iterate through new input mapping until you find a matching FQN
                        for (Map.Entry<String, Object> entry : newInputJson.entrySet()) {
                                if (paramName.equals(entry.getKey())) {
                                        isNew = true;
                                        try {
                                                newJSON.put(entry.getKey(), entry.getValue());
                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }
                                        break;
                                }
                        }

                        // If not a file, will just add as is
                        if (!isNew) {
                                try {
                                        newJSON.put(paramName, currentParam);
                                } catch (JSONException e) {
                                        e.printStackTrace();
                                }
                        }
                }

                // Now make a new file
                writeJob("foo2.json", newJSON);
                File newFile = new File("foo2.json");
                return newFile.getAbsolutePath();

        }

        /**
         * Writes a given JSON object to new file jobOutputPath
         * @param jobOutputPath Path to output JSON to
         * @param newJson JSON object to be saved to file
         */
        private void writeJob(String jobOutputPath, JSONObject newJson) {
                try {
                        //TODO: investigate, why is this replacement occurring?
                        final String replace = newJson.toString().replace("\\", "");
                        FileUtils.writeStringToFile(new File(jobOutputPath), replace, StandardCharsets.UTF_8);
                } catch (IOException e) {
                        throw new RuntimeException("Could not write job ", e);
                }
        }
}
