/*
 * Copyright (c) 2015,robinjim(robinjim@126.com)
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
package com.robin.comm.script;

import java.util.Map;

import javax.script.Bindings;
import javax.script.CompiledScript;

public interface IscriptExecutor {
	public CompiledScript returnScript(String stepId,String scripts) throws Exception;
	public Bindings createBindings();
	public Object invokeFunction(Map<String, Object> contextMap,String function,String name,Object[] params) throws Exception;
}
