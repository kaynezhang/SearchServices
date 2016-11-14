package org.alfresco.rest.people;

import org.alfresco.rest.RestTest;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.ErrorModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.testrail.ExecutionType;
import org.alfresco.utility.testrail.annotation.TestRail;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for Get Favorite Sites (/people/{personId}/preferences) RestAPI call
 *  
 * @author Cristina Axinte
 *
 */
@Test(groups = { TestGroup.REST_API, TestGroup.PEOPLE, TestGroup.SANITY })
public class GetFavoriteSiteSanityTests extends RestTest
{
    UserModel userModel;
    SiteModel siteModel1;
    SiteModel siteModel2;
    UserModel searchedUser;

    @BeforeClass(alwaysRun=true)
    public void dataPreparation() throws Exception
    {
        userModel = dataUser.createRandomTestUser();
        siteModel1 = dataSite.usingUser(userModel).createPublicRandomSite();
        siteModel2 = dataSite.usingUser(userModel).createPublicRandomSite();
    }

    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify manager user gets its specific favorite site with Rest API and response is successful (200)")
    public void managerUserGetsFavoriteSiteWithSuccess() throws Exception
    {
        UserModel managerUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(managerUser, siteModel1, UserRole.SiteManager);
        dataSite.usingUser(managerUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(managerUser).usingSite(siteModel2).addSiteToFavorites();

        restClient.authenticateUser(managerUser)  
                  .usingAuthUser().getFavoriteSite(siteModel1)
                  .assertThat().field("id").is(siteModel1.getId())
                  .and().field("title").isNotNull();
        restClient.assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify collaborator user gets its specific favorite site with Rest API and response is successful (200)")
    public void collaboratorUserGetsFavoriteSiteWithSuccess() throws Exception
    {
        UserModel collaboratorUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(collaboratorUser, siteModel1, UserRole.SiteCollaborator);
        dataSite.usingUser(collaboratorUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(collaboratorUser).usingSite(siteModel2).addSiteToFavorites();

        restClient.authenticateUser(collaboratorUser)
                  .usingAuthUser().getFavoriteSite(siteModel1)
                	.assertThat().field("id").is(siteModel1.getId())
                	.and().field("title").isNotNull();
        restClient.assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify contributor user gets its specific favorite site with Rest API and response is successful (200)")
    public void contributorUserGetsFavoriteSiteWithSuccess() throws Exception
    {
        UserModel contributorUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(contributorUser, siteModel1, UserRole.SiteContributor);
        dataSite.usingUser(contributorUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(contributorUser).usingSite(siteModel2).addSiteToFavorites();

        restClient.authenticateUser(contributorUser)
                  .usingAuthUser().getFavoriteSite(siteModel1)
                	.assertThat().field("id").is(siteModel1.getId())
                	.and().field("title").isNotNull();
        restClient.assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify consumer user gets its specific favorite site with Rest API and response is successful (200)")
    public void consumerUserGetsFavoriteSiteWithSuccess() throws Exception
    {
        UserModel consumerUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(consumerUser, siteModel1, UserRole.SiteConsumer);
        dataSite.usingUser(consumerUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(consumerUser).usingSite(siteModel2).addSiteToFavorites();

        restClient.authenticateUser(consumerUser)
                  .usingAuthUser().getFavoriteSite(siteModel1)
                	.assertThat().field("id").is(siteModel1.getId())
                	.and().field("title").isNotNull();
        restClient.assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify admin user gets specific favorite site of any user with Rest API and response is successful (200)")
    public void adminUserGetsAnyFavoriteSiteWithSuccess() throws Exception
    {
        UserModel adminUset = dataUser.getAdminUser();
        UserModel anyUser = dataUser.usingAdmin().createRandomTestUser();
        dataSite.usingUser(anyUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(anyUser).usingSite(siteModel2).addSiteToFavorites();

        restClient.authenticateUser(adminUset)
                  .usingAuthUser().getFavoriteSite(siteModel1)
                	.assertThat().field("id").is(siteModel1.getId())
                	.and().field("title").isNotNull();
        restClient.assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify manager user fails to get specific favorite site of another user with Rest API and response is successful (403)")
    public void managerUserFailsToGetFavoriteSiteOfAnotherUser() throws Exception
    {
        UserModel managerUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(managerUser, siteModel1, UserRole.SiteManager);
        dataSite.usingUser(managerUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(managerUser).usingSite(siteModel2).addSiteToFavorites();

        restClient.authenticateUser(managerUser)
                  .usingAuthUser().getFavoriteSite(siteModel1);
        restClient.assertStatusCodeIs(HttpStatus.FORBIDDEN).assertLastError().containsSummary(ErrorModel.PERMISSION_WAS_DENIED);
    }
    
    @Bug(id = "MNT-16904")
    @TestRail(section = { TestGroup.REST_API, TestGroup.PEOPLE }, executionType = ExecutionType.SANITY, description = "Verify manager user is NOT Authorized gets its specific favorite site with Rest API when authentication fails (401)")
    public void managerUserNotAuthorizedFailsToGetFavoriteSite() throws Exception
    {
        UserModel managerUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel).addUserToSite(managerUser, siteModel1, UserRole.SiteManager);
        dataSite.usingUser(managerUser).usingSite(siteModel1).addSiteToFavorites();
        dataSite.usingUser(managerUser).usingSite(siteModel2).addSiteToFavorites();
        managerUser.setPassword("newpassword");

        restClient.authenticateUser(managerUser)
                  .usingAuthUser().getFavoriteSite(siteModel1);
        restClient.assertStatusCodeIs(HttpStatus.UNAUTHORIZED);
    }
}
