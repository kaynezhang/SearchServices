package org.alfresco.rest.tags;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.rest.RestTest;
import org.alfresco.rest.exception.JsonToModelConversionException;
import org.alfresco.rest.model.RestTagModel;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.ErrorModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.StatusModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.testrail.ExecutionType;
import org.alfresco.utility.testrail.annotation.TestRail;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 10/4/2016.
 */
@Test(groups = { TestGroup.REST_API, TestGroup.TAGS, TestGroup.SANITY })
public class DeleteTagSanityTests extends RestTest
{
    private UserModel adminUserModel, userModel;
    private SiteModel siteModel;
    private DataUser.ListUserWithRoles usersWithRoles;
    private RestTagModel tag;
    private FileModel document, contributorDoc;

    @BeforeClass(alwaysRun=true)
    public void dataPreparation() throws Exception
    {
        adminUserModel = dataUser.getAdminUser();
        siteModel = dataSite.usingUser(adminUserModel).createPublicRandomSite();
        document = dataContent.usingSite(siteModel).usingUser(adminUserModel).createContent(CMISUtil.DocumentType.TEXT_PLAIN);
        usersWithRoles = dataUser.addUsersWithRolesToSite(siteModel, UserRole.SiteManager, UserRole.SiteCollaborator, UserRole.SiteConsumer, UserRole.SiteContributor);
    }

    @TestRail(section = { TestGroup.REST_API,
            TestGroup.TAGS }, executionType = ExecutionType.SANITY, description = "Verify Admin user deletes tags with Rest API and status code is 204")
    public void adminIsAbleToDeleteTags() throws JsonToModelConversionException, Exception
    {
        restClient.authenticateUser(adminUserModel);
        tag = restClient.withCoreAPI().usingResource(document).addTag(RandomData.getRandomName("tag"));
        
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.NO_CONTENT);
    }

    @TestRail(section = { TestGroup.REST_API,
            TestGroup.TAGS }, executionType = ExecutionType.SANITY, description = "Verify Manager user deletes tags created by admin user with Rest API and status code is 204")
    public void managerIsAbleToDeleteTags() throws JsonToModelConversionException, Exception
    {
        restClient.authenticateUser(adminUserModel);
        tag = restClient.withCoreAPI().usingResource(document).addTag(RandomData.getRandomName("tag"));
        
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteManager));
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.NO_CONTENT);
    }

    @TestRail(section = { TestGroup.REST_API,
            TestGroup.TAGS }, executionType = ExecutionType.SANITY, description = "Verify Collaborator user deletes tags created by admin user with Rest API and status code is 204")
    public void collaboratorIsAbleToDeleteTags() throws JsonToModelConversionException, Exception
    {
        restClient.authenticateUser(adminUserModel);
        tag = restClient.withCoreAPI().usingResource(document).addTag(RandomData.getRandomName("tag"));

        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteCollaborator));
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.NO_CONTENT);
    }

    @TestRail(section = { TestGroup.REST_API, TestGroup.TAGS },
            executionType = ExecutionType.SANITY, description = "Verify Contributor user can't delete tags created by admin user with Rest API and status code is 403")
    public void contributorIsNotAbleToDeleteTagsForAnotherUserContent() throws JsonToModelConversionException, Exception
    {
        restClient.authenticateUser(adminUserModel);
        tag = restClient.withCoreAPI().usingResource(document).addTag(RandomData.getRandomName("tag"));

        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteContributor));
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.FORBIDDEN).assertLastError().containsSummary(ErrorModel.PERMISSION_WAS_DENIED);
    }

    @TestRail(section = { TestGroup.REST_API,
            TestGroup.TAGS }, executionType = ExecutionType.SANITY, description = "Verify Contributor user deletes tags created by him with Rest API and status code is 204")
    public void contributorIsAbleToDeleteTagsForHisContent() throws JsonToModelConversionException, Exception
    {
        userModel = usersWithRoles.getOneUserWithRole(UserRole.SiteContributor);
        restClient.authenticateUser(userModel);
        contributorDoc = dataContent.usingSite(siteModel).usingUser(userModel).createContent(CMISUtil.DocumentType.TEXT_PLAIN);;
        tag = restClient.withCoreAPI().usingResource(contributorDoc).addTag(RandomData.getRandomName("tag"));
        
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.NO_CONTENT);
    }

    @TestRail(section = { TestGroup.REST_API,
            TestGroup.TAGS }, executionType = ExecutionType.SANITY, description = "Verify Consumer user can't delete tags created by admin user with Rest API and status code is 403")
    public void consumerIsNotAbleToDeleteTags() throws JsonToModelConversionException, Exception
    {
        restClient.authenticateUser(adminUserModel);
        tag = restClient.withCoreAPI().usingResource(document).addTag(RandomData.getRandomName("tag"));

        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteConsumer));
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.FORBIDDEN).assertLastError().containsSummary(ErrorModel.PERMISSION_WAS_DENIED);
    }

    @Bug(id="MNT-16904")
    @TestRail(section = { TestGroup.REST_API,
            TestGroup.TAGS }, executionType = ExecutionType.SANITY, description = "Verify user gets status code 401 if authentication call fails")
    public void userIsNotAbleToDeleteTagIfAuthenticationFails() throws JsonToModelConversionException, Exception
    {
        restClient.authenticateUser(adminUserModel);
        tag = restClient.withCoreAPI().usingResource(document).addTag(RandomData.getRandomName("tag"));

        UserModel siteManager = usersWithRoles.getOneUserWithRole(UserRole.SiteManager);
        siteManager.setPassword("wrongPassword");
        restClient.authenticateUser(siteManager);
        restClient.withCoreAPI().usingResource(document).deleteTag(tag);
        restClient.assertStatusCodeIs(HttpStatus.UNAUTHORIZED).assertLastException().hasName(StatusModel.UNAUTHORIZED);
    }
}