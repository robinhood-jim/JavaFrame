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
package com.robin.core.base.exception;

public class WebException extends AbstractCodeException {
    private static final long serialVersionUID = 1L;

    public WebException() {
        super(500);
    }

    public WebException(String s) {
        super(500,s);
    }

    public WebException(Exception e) {
        super(e);
    }
    public WebException(int retCode,String message){
        super(retCode,message);
    }

}
