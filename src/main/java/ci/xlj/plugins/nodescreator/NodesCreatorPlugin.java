/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License") you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//      Contributors:      Xu Lijia

package ci.xlj.plugins.nodescreator;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Api;
import hudson.model.Node.Mode;
import hudson.slaves.NodeProperty;
import hudson.slaves.DumbSlave;
import hudson.slaves.JNLPLauncher;
import hudson.slaves.RetentionStrategy;

import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.interceptor.RequirePOST;

import ci.xlj.libs.utils.StringUtils;

/**
 * @author kfzx-xulj
 *
 */
@Extension
@ExportedBean
public class NodesCreatorPlugin extends Plugin {

	public Api getApi() {
		return new Api(this);
	}

	@Exported
	public boolean getExistence() {
		return false;
	}

	private Jenkins jenkins = Jenkins.getInstance();

	@RequirePOST
	public void doCreateNode(StaplerRequest req, StaplerResponse res) {
		String[] parameters=null;
		try {
			Scanner in = new Scanner(req.getInputStream());
			StringBuilder b = new StringBuilder();
			while (in.hasNext()) {
				b.append(in.next());
			}
			in.close();
			
			parameters=b.toString().split(",");
		} catch (IOException e) {
			e.printStackTrace();
		}

		String nodeName = parameters[0];
		String description = parameters[1];
		String nodeHome = parameters[2];
		String numExecutors = parameters[3];
		String label = parameters[4];

		synchronized (jenkins) {
			DumbSlave slave;
			try {
				slave = new DumbSlave(nodeName, description, nodeHome,
						numExecutors, Mode.NORMAL, label, new JNLPLauncher(),
						RetentionStrategy.NOOP,
						Collections.<NodeProperty<?>> emptyList());
				jenkins.addNode(slave);
			} catch (Exception e) {
				System.err.println(StringUtils.getStrackTrace(e));
			}
		}
	}

}
