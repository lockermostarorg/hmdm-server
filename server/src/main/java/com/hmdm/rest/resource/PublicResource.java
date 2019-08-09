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

package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hmdm.persistence.CustomerDAO;
import com.hmdm.persistence.UnsecureDAO;
import com.hmdm.persistence.domain.Application;
import com.hmdm.persistence.domain.Customer;
import com.hmdm.persistence.domain.Device;
import com.hmdm.rest.json.Response;
import com.hmdm.rest.json.UploadAppRequest;
import com.hmdm.util.CryptoUtil;
import com.hmdm.util.StringUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.hmdm.util.FileUtil.writeToFile;

/**
 * <p>A publicly available API which does not require authentication/authorization.</p>
 *
 * @author isv
 */
@Singleton
@Path("/public")
@Api(tags = {"Mobile client API"})
public class PublicResource {

    private static final Logger logger  = LoggerFactory.getLogger(PublicResource.class);

    private UnsecureDAO unsecureDAO;
    private CustomerDAO customerDAO;
    private String hashSecret;
    private String filesDirectory;
    private String baseUrl;

    /**
     * <p>A constructor required by Swagger.</p>
     */
    public PublicResource() {
    }

    /**
     * <p>Constructs new <code>PublicResource</code> instance. This implementation does nothing.</p>
     */
    @Inject
    public PublicResource(@Named("files.directory") String filesDirectory,
                          @Named("base.url") String baseUrl,
                          UnsecureDAO unsecureDAO,
                          CustomerDAO customerDAO,
                          @Named("hash.secret") String hashSecret) {
        this.filesDirectory = filesDirectory;
        this.baseUrl = baseUrl;
        this.unsecureDAO = unsecureDAO;
        this.customerDAO = customerDAO;
        this.hashSecret = hashSecret;
    }
    
    // =================================================================================================================
    @ApiOperation(
            value = "Upload application",
            notes = "Uploads application specification to MDM server"
    )
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/applications/upload")
    public Response uploadFiles(@FormDataParam("file") InputStream uploadedInputStream,
                                @FormDataParam("file") @ApiParam("A file to upload") FormDataContentDisposition fileDetail,
                                @FormDataParam("app") @ApiParam("A JSON-string with application details") String app) throws Exception {

        logger.info("Received Upload App request. App: {}", app);

        ObjectMapper objectMapper = new ObjectMapper();
        UploadAppRequest request = objectMapper.readValue(app, UploadAppRequest.class);

        String deviceId = StringUtil.stripOffTrailingCharacter(request.getDeviceId(), "\"");
        String hash = StringUtil.stripOffTrailingCharacter(request.getHash(), "\"");

        List<String> errors = new ArrayList<>();
        if (request.getName() == null || request.getName().isEmpty()) {
            errors.add("name");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            errors.add("name");
        }
        if (request.getPkg() == null || request.getPkg().isEmpty()) {
            errors.add("pkg");
        }
        if (request.getVersion() == null || request.getVersion().isEmpty()) {
            errors.add("version");
        }
        if (fileDetail != null && !fileDetail.getFileName().isEmpty() && uploadedInputStream != null) {
            if (request.getLocalPath() == null || request.getLocalPath().isEmpty()) {
                errors.add("localPath");
            }
            if (request.getFileName() == null || request.getFileName().isEmpty()) {
                errors.add("fileName");
            }
        }
        if (deviceId == null || deviceId.isEmpty()) {
            errors.add("deviceId");
        }
        if (hash == null || hash.isEmpty()) {
            errors.add("hash");
        }

        if (!errors.isEmpty()) {
            logger.error("Не указаны требуемые данные: {}", errors);
            return Response.ERROR("error.params.missing", errors);
        }

        String expectedHash = CryptoUtil.getMD5String(deviceId + this.hashSecret);
        if (!expectedHash.equalsIgnoreCase(hash)) {
            logger.error("Hash invalid for upload app request from device {}. Expected: {} but got {}",
                    deviceId, expectedHash, hash.toUpperCase());
//            System.out.println("Hash invalid: " + expectedHash + " vs " + hash);
//            return Response.ERROR("Invalid hash"); // TODO : ISV : Commented for now #5854
        }

        // Find device and get the customer
        Device dbDevice = this.unsecureDAO.getDeviceByNumber(deviceId);
        if (dbDevice == null) {
            logger.error("Device not found: {}", deviceId);
            return Response.DEVICE_NOT_FOUND_ERROR();
        }

        // Check for duplicate package ID
        List<Application> dbApps = this.unsecureDAO.findByPackageIdAndVersion(
                dbDevice.getCustomerId(), request.getPkg(), request.getVersion()
        );
        if (!dbApps.isEmpty()) {
            logger.error("Application with same package ID and version already exists: {} v{}", request.getPkg(), request.getVersion());
            return Response.DUPLICATE_APPLICATION();
        }

        Customer customer = this.customerDAO.findById(dbDevice.getCustomerId());

        boolean fileUploaded = false;
        if (fileDetail != null && !fileDetail.getFileName().isEmpty() && uploadedInputStream != null) {
            File uploadFile = new File(
                    new File(new File(this.filesDirectory, customer.getFilesDir()), request.getLocalPath()), request.getFileName()
            );

            File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            if (parentFile.exists() && parentFile.isDirectory()) {
                writeToFile(uploadedInputStream, uploadFile.getAbsolutePath());
                fileUploaded = true;
            } else {
                logger.error("Can not save the file on server in directory: {}", parentFile);
                return Response.INTERNAL_ERROR();
            }
        }

        Application application = new Application();
        application.setName(request.getName());
        application.setPkg(request.getPkg());
        application.setShowIcon(request.isShowIcon());
        application.setVersion(request.getVersion());
        application.setCustomerId(dbDevice.getCustomerId());

        if (fileUploaded) {
            String url = String.format("%s/files/%s/%s/%s", this.baseUrl,
                    URLEncoder.encode(customer.getFilesDir(), "UTF8"),
                    URLEncoder.encode(request.getLocalPath(), "UTF8"),
                    URLEncoder.encode(request.getFileName(), "UTF8"));
            application.setUrl(url);
        }

        this.unsecureDAO.insertApplication(application);

        logger.info("Application {} has been uploaded to server from device {} successfully", application, deviceId);

        return Response.OK();
    }

}
