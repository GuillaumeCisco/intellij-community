/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.execution.testframework.history;

import com.intellij.execution.Location;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.testframework.sm.runner.TestProxyFilterProvider;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ImportedTestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
  private final SMTRunnerConsoleProperties myProperties;
  private final File myFile;
  private final ProcessHandler myHandler;

  public ImportedTestConsoleProperties(SMTRunnerConsoleProperties properties,
                                       File file,
                                       ProcessHandler handler) {
    super(properties.getConfiguration(), properties.getTestFrameworkName(), properties.getExecutor());
    myProperties = properties;
    myFile = file;
    myHandler = handler;
  }

  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                      @NotNull TestConsoleProperties consoleProperties) {
    return new ImportedToGeneralTestEventsConverter(testFrameworkName, consoleProperties, myFile, myHandler);
  }

  @Override
  public boolean isIdBasedTestTree() {
    return false;
  }

  @Override
  public boolean isPrintTestingStartedTime() {
    return false;
  }

  @Nullable
  @Override
  public Navigatable getErrorNavigatable(@NotNull Location<?> location,
                                         @NotNull String stacktrace) {
    return myProperties.getErrorNavigatable(location, stacktrace);
  }

  @Override
  public Navigatable getErrorNavigatable(@NotNull Project project,
                                         @NotNull String stacktrace) {
    return myProperties.getErrorNavigatable(project, stacktrace);
  }

  @Override
  public void addStackTraceFilter(Filter filter) {
    myProperties.addStackTraceFilter(filter);
  }

  @Override
  public boolean fixEmptySuite() {
    return myProperties.fixEmptySuite();
  }

  @Override
  @Nullable
  public SMTestLocator getTestLocator() {
    return myProperties.getTestLocator();
  }

  @Override
  @Nullable
  public TestProxyFilterProvider getFilterProvider() {
    return myProperties.getFilterProvider();
  }

  @Override
  @Nullable
  public AbstractRerunFailedTestsAction createRerunFailedTestsAction(ConsoleView consoleView) {
    return myProperties.createRerunFailedTestsAction(consoleView);
  }
}
