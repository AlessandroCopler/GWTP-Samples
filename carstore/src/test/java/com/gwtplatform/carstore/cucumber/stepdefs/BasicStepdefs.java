/*
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.cucumber.stepdefs;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Strings;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.cucumber.application.ApplicationPage;
import com.gwtplatform.carstore.cucumber.application.login.LoginPage;
import com.gwtplatform.carstore.cucumber.application.widgets.MessageWidgetPage;
import com.gwtplatform.carstore.cucumber.util.TestParameters;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.java.guice.ScenarioScoped;

import static org.junit.Assert.assertTrue;

import static com.gwtplatform.carstore.client.debug.DebugIds.DBG_LOGIN;

@ScenarioScoped
public class BasicStepdefs {
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "qwerty";
    private static final String INVALID_USERNAME = "--";
    private static final String INVALID_PASSWORD = "--";

    private final WebDriver webDriver;
    private final LoginPage loginPage;
    private final ApplicationPage applicationPage;
    private final MessageWidgetPage messageWidgetPage;

    @Inject
    BasicStepdefs(
            WebDriver webDriver,
            LoginPage loginPage,
            ApplicationPage applicationPage,
            MessageWidgetPage messageWidgetPage) {
        this.webDriver = webDriver;
        this.loginPage = loginPage;
        this.applicationPage = applicationPage;
        this.messageWidgetPage = messageWidgetPage;
    }

    @After
    public void cleanup() {
        webDriver.quit();
    }

    @Given("^I navigate to the (\\S+) page$")
    public void iNavigateTo(String nameToken) {
        if (NameTokens.LOGIN.equals(nameToken)) {
            applicationPage.navigateToNotLoggedIn(nameToken);
        } else {
            applicationPage.navigateToWithMenu(nameToken);
        }
    }

    @Given("^I try to navigate to the (\\S+) page while not logged in$")
    public void iTryToNavigateTo(String nameToken) {
        applicationPage.navigateToNotLoggedIn(nameToken);
    }

    @Given("^I'm logged in$")
    public void iAmLoggedIn() {
        iNavigateTo(DBG_LOGIN);
        enterValidCredential("valid");
        applicationPage.waitUntilDomIsLoaded("manufacturer");
    }

    @When("^I enter (\\S+) credential$")
    public void enterValidCredential(String valid) {
        if (valid.equals("valid")) {
            loginPage.setUsername(VALID_USERNAME);
            loginPage.setPassword(VALID_PASSWORD);
        } else if (valid.equals("semivalid")) {
            loginPage.setUsername(VALID_USERNAME);
            loginPage.setPassword(INVALID_PASSWORD);
        } else {
            loginPage.setUsername(INVALID_USERNAME);
            loginPage.setPassword(INVALID_PASSWORD);
        }
        loginPage.submitLoginForm();
    }

    @Then("^I should be on the (\\S+) page$")
    public void iShouldBeOnThePage(String nameToken) throws Throwable {
        String url = TestParameters.BASE_URL + "#" + nameToken;

        applicationPage.waitUntilDomIsLoaded(nameToken);

        assertTrue(webDriver.getCurrentUrl().startsWith(url));
    }

    @Then("^I see a success message containing (.*?)( disappear){0,1}$")
    public void I_see_a_success_message_containing(String message, String disappear) {
        assertTrue(applicationPage.successMessageIsPresent(message));
        if (Strings.nullToEmpty(disappear).equals(" disappear")) {
            messageWidgetPage.waitUntilSuccessMessageIsHidden(message);
        }
    }
}
