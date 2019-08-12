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
package com.robin.meta.contorller;

import com.robin.core.fileaccess.meta.DataCollectionMeta;
import com.robin.meta.service.GlobalResourceService;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/schema")
public class SchemaController {
    @Autowired
    private GlobalResourceService globalResourceService;
    @RequestMapping("/resource")
    @ResponseBody
    public Map<String,Object> getResourceSchema(@RequestParam Long sourceId,@RequestParam String sourceParam){
        DataCollectionMeta collectionMeta=globalResourceService.getResourceMetaDef(2L);
        //Schema schema=globalResourceService.getDataSourceSchema(collectionMeta,sourceId,sourceParam);
        Map<String,Object> retMap=new HashMap<>();
        retMap.put("schema",globalResourceService.getDataSourceSchemaDesc(collectionMeta,sourceId,sourceParam,0));
        return retMap;
    }
}