/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.rengine.service.deploy;

import static com.wl4g.rengine.common.constants.RengineConstants.*;

import java.io.Console;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wl4g.rengine.service.security.user.AuthenticationService;

/**
 * Tool to reset the root password.
 *
 * @author James Wong
 */
@Component
public class RengineRootPasswordTool implements CommandLineRunner {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String SEPARATOR = "---------------------------------------";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(SEPARATOR);
        System.out.println("Rengine Root Password Reset Tool");
        System.out.println(SEPARATOR);

        // ... rest of the method
    }

    // ... other methods using SEPARATOR instead of the literal
}
