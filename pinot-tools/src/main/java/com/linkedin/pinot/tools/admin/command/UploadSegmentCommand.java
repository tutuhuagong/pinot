/**
 * Copyright (C) 2014-2015 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.tools.admin.command;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.Option;

import com.linkedin.pinot.common.utils.FileUploadUtils;
import com.linkedin.pinot.common.utils.TarGzCompressionUtils;


/**
 * Class for command to upload data into Pinot.
 *
 *
 */
public class UploadSegmentCommand extends AbstractBaseCommand implements Command {
  @Option(name = "-controllerPort", required = false, metaVar = "<int>", usage = "Port number for controller.")
  private String _controllerPort = DEFAULT_CONTROLLER_PORT;

  @Option(name = "-segmentDir", required = true, metaVar = "<string>", usage = "Path to segment directory.")
  private String _segmentDir = null;

  @Option(name = "-help", required = false, help = true, aliases={"-h", "--h", "--help"}, usage = "Print this message.")
  private boolean _help = false;

  public boolean getHelp() {
    return _help;
  }

  private String _controllerHost = "localhost";

  @Override
  public String getName() {
    return "UploadSegment";
  }

  @Override
  public String toString() {
    return ("UploadSegment " + " -controllerPort " + _controllerPort + " -segmentDir " + _segmentDir);
  }

  @Override
  public void cleanup() {

  }

  @Override
  public String description() {
    return "Upload the Pinot segments.";
  }

  public UploadSegmentCommand setControllerHost(String controllerHost) {
    _controllerHost = controllerHost;
    return this;
  }

  public UploadSegmentCommand setControllerPort(String controllerPort) {
    _controllerPort = controllerPort;
    return this;
  }

  public UploadSegmentCommand setSegmentDir(String segmentDir) {
    _segmentDir = segmentDir;
    return this;
  }

  @Override
  public boolean execute() throws Exception {
    File dir = new File(_segmentDir);
    File[] files = dir.listFiles();

    for (File file : files) {
      if (!file.isDirectory()) {
        continue;
      }

      String srcDir = file.getAbsolutePath();

      String outFile = TarGzCompressionUtils.createTarGzOfDirectory(srcDir);
      File tgzFile = new File(outFile);
      FileUploadUtils.sendSegmentFile(_controllerHost, _controllerPort, tgzFile.getName(),
          new FileInputStream(tgzFile), tgzFile.length());

      FileUtils.deleteQuietly(tgzFile);
    }

    return true;
  }
}
