/*
 *
 * Headwind MDM: Open Source Android MDM Software
 * https://h-mdm.com
 *
 * Copyright (C) 2019 Headwind Solutions LLC (http://h-sms.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@ApiModel(description = "A specification of a single application installed and used on mobile device")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CustomerData, Serializable {

    private static final long serialVersionUID = -911919013171848302L;

    @ApiModelProperty("An application ID")
    private Integer id;
    @ApiModelProperty("A name of application")
    private String name;
    @ApiModelProperty("A package ID of application")
    private String pkg;
    @ApiModelProperty("A version of application")
    private String version;
    @ApiModelProperty("An URL for application package")
    private String url;
    @ApiModelProperty("A flag indicating if icon is to be shown on mobile device")
    private boolean showIcon;
    @ApiModelProperty("A flag indicating if application is a system application")
    private boolean system;
    @ApiModelProperty("A list of configurations using the application")
    private List<Configuration> configurations = new LinkedList<>();
    @ApiModelProperty("An ID of a most recent version for application")
    private Integer latestVersion;
    @ApiModelProperty("A flag indicating if application must be run after installation")
    private boolean runAfterInstall;
    @ApiModelProperty("A flag indicating if version check must be skipped for application")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean skipVersion;

    // A flag indicating that application is to be removed from the application
    // This field is going to be removed as now action field is stored in DB and encodes the removed apps with
    // value of 2
    @ApiModelProperty(hidden = true)
    @Deprecated
    private boolean remove;
    @ApiModelProperty(hidden = true)
    private boolean selected;
    @ApiModelProperty(hidden = true)
    private int customerId;
    @ApiModelProperty(hidden = true)
    private String customerName;
    @ApiModelProperty(hidden = true)
    private boolean commonApplication;
    @ApiModelProperty(hidden = true)
    private boolean deletionProhibited;
    @ApiModelProperty(hidden = true)
    private boolean outdated;
    @ApiModelProperty(hidden = true)
    private String latestVersionText;
    @ApiModelProperty(hidden = true)
    private Integer usedVersionId;

    /**
     * <p>A path to uploaded file to link this application to when adding an application.</p>
     */
    @ApiModelProperty(hidden = true)
    private String filePath;

    // A helper property to indicate the action required to be performed by mobile device
    // in regard to application installation
    // 0 - do not install and hide if installed
    // 1 - install
    // 2 - do not install and remove if installed
    @ApiModelProperty(value = "", allowableValues = "0,1,2")
    private int action;

    public Application() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkg() {
        return this.pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getShowIcon() {
        return this.showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isCommonApplication() {
        return commonApplication;
    }

    public void setCommonApplication(boolean commonApplication) {
        this.commonApplication = commonApplication;
    }

    public boolean isCommon() {
        return this.isCommonApplication();
    }

    @Deprecated
    public boolean isRemove() {
        return remove;
    }

    @Deprecated
    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isDeletionProhibited() {
        return deletionProhibited;
    }

    public void setDeletionProhibited(boolean deletionProhibited) {
        this.deletionProhibited = deletionProhibited;
    }

    public Integer getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Integer latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public String getLatestVersionText() {
        return latestVersionText;
    }

    public void setLatestVersionText(String latestVersionText) {
        this.latestVersionText = latestVersionText;
    }

    public Integer getUsedVersionId() {
        return usedVersionId;
    }

    public void setUsedVersionId(Integer usedVersionId) {
        this.usedVersionId = usedVersionId;
    }

    public boolean isRunAfterInstall() {
        return runAfterInstall;
    }

    public void setRunAfterInstall(boolean runAfterInstall) {
        this.runAfterInstall = runAfterInstall;
    }

    public Boolean isSkipVersion() {
        return skipVersion;
    }

    public void setSkipVersion(Boolean skipVersion) {
        this.skipVersion = skipVersion;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pkg='" + pkg + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", showIcon=" + showIcon +
                ", runAfterInstall=" + runAfterInstall +
                ", skipVersion=" + skipVersion +
                ", system=" + system +
                ", configurations=" + configurations +
                ", customerId=" + customerId +
                ", filePath='" + filePath + '\'' +
                ", deletionProhibited='" + deletionProhibited + '\'' +
                ", outdated='" + outdated + '\'' +
                ", latestVersion='" + latestVersion + '\'' +
                ", latestVersionText='" + latestVersionText + '\'' +
                ", usedVersionId='" + usedVersionId + '\'' +
                '}';
    }
}