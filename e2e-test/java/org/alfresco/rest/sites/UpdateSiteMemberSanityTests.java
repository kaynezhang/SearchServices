package org.alfresco.rest.sites;

import org.alfresco.rest.RestTest;
import org.alfresco.rest.requests.RestSitesApi;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataUser.ListUserWithRoles;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.testrail.ExecutionType;
import org.alfresco.utility.testrail.annotation.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = { "rest-api", "sites", "sanity" })
public class UpdateSiteMemberSanityTests extends RestTest
{
    @Autowired
    RestSitesApi siteAPI;

    private UserModel adminUserModel;
    private SiteModel siteModel;
    private ListUserWithRoles usersWithRoles;
    private UserModel testUserModel;

    @BeforeClass(alwaysRun=true)
    public void dataPreparation() throws Exception
    {
        adminUserModel = dataUser.getAdminUser();
        siteAPI.useRestClient(restClient);
        siteModel = dataSite.usingUser(adminUserModel).createPublicRandomSite();
        usersWithRoles = dataUser.addUsersWithRolesToSite(siteModel, UserRole.SiteManager, UserRole.SiteCollaborator, UserRole.SiteConsumer,
                UserRole.SiteContributor);
        testUserModel = dataUser.createRandomTestUser();
        dataUser.addUserToSite(testUserModel, siteModel, UserRole.SiteConsumer);
    }

    @TestRail(section = {"rest-api", "sites" }, executionType = ExecutionType.SANITY, 
            description = "Verify that manager is able to update site member and gets status code OK (200)")
    public void managerIsAbleToUpdateSiteMember() throws Exception
    {
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteManager));
        testUserModel.setUserRole(UserRole.SiteConsumer);
        siteAPI.updateSiteMember(siteModel, testUserModel);
        siteAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @Bug(id="ACE-5444")
    @TestRail(section = {"rest-api", "sites" }, executionType = ExecutionType.SANITY, 
            description = "Verify that collaborator is not able to update site member and gets status code FORBIDDEN (403)")
    public void collaboratorIsNotAbleToUpdateSiteMember() throws Exception
    {
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteCollaborator));
        testUserModel.setUserRole(UserRole.SiteCollaborator);
        siteAPI.updateSiteMember(siteModel, testUserModel);
        siteAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.FORBIDDEN);
    }
    
    @Bug(id="ACE-5444")
    @TestRail(section = {"rest-api", "sites" }, executionType = ExecutionType.SANITY, 
            description = "Verify that contributor is not able to update site member and gets status code FORBIDDEN (403)")
    public void contributorIsNotAbleToUpdateSiteMember() throws Exception
    {
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteContributor));
        testUserModel.setUserRole(UserRole.SiteCollaborator);
        siteAPI.updateSiteMember(siteModel, testUserModel);
        siteAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.FORBIDDEN);
    }
    
    @Bug(id="ACE-5444")
    @TestRail(section = {"rest-api", "sites" }, executionType = ExecutionType.SANITY, 
            description = "Verify that consumer is not able to update site member and gets status code FORBIDDEN (403)")
    public void consumerIsNotAbleToUpdateSiteMember() throws Exception
    {
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteConsumer));
        testUserModel.setUserRole(UserRole.SiteCollaborator);
        siteAPI.updateSiteMember(siteModel, testUserModel);
        siteAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.FORBIDDEN);
    }
    
    @TestRail(section = {"rest-api", "sites" }, executionType = ExecutionType.SANITY, 
            description = "Verify that admin is able to update site member and gets status code OK (200)")
    public void adminIsAbleToUpdateSiteMember() throws Exception
    {
        restClient.authenticateUser(adminUserModel);
        testUserModel.setUserRole(UserRole.SiteCollaborator);
        siteAPI.updateSiteMember(siteModel, testUserModel);
        siteAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @Bug(id="MNT-16904")
    @TestRail(section = {"rest-api", "sites" }, executionType = ExecutionType.SANITY, 
            description = "Verify that unauthenticated user is not able to update site member")
    public void unauthenticatedUserIsNotAuthorizedToUpdateSiteMmeber() throws Exception{
        UserModel inexistentUser = new UserModel("inexistent user", "inexistent password");
        restClient.authenticateUser(inexistentUser);
        testUserModel.setUserRole(UserRole.SiteCollaborator);
        siteAPI.updateSiteMember(siteModel, testUserModel);
        siteAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.UNAUTHORIZED);
    }
}
