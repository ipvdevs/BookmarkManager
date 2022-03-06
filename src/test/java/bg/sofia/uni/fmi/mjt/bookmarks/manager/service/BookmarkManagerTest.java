package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.HtmlParser;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.UrlShortenerService;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.BookmarkStorage;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookmarkManagerTest {
    public static final String CALLER = BookmarkManagerTest.class.toString();

    BookmarkStorage storage = new BookmarkStorage();

    UrlShortenerService shortener = Mockito.mock(UrlShortenerService.class);
    HtmlParser parser = Mockito.mock(HtmlParser.class);

    HttpClient client = Mockito.mock(HttpClient.class);
    HttpResponse<String> responseMock = (HttpResponse<String>) Mockito.mock(HttpResponse.class);

    @InjectMocks
    BookmarkManager manager = new BookmarkManager(storage, shortener, parser, client);

    @Test
    void createGroup() {
        assertEquals(
                Status.OK,
                manager.createGroup("createGroup", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");
    }

    @Test
    void createGroupDuplicate() {
        assertEquals(
                Status.OK,
                manager.createGroup("createGroupDuplicate", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.ERROR,
                manager.createGroup("createGroupDuplicate", CALLER).status(),
                "No duplicates are allowed.");
    }

    @Test
    void createGroupNullArgs() {
        assertEquals(
                Status.ERROR,
                manager.createGroup(null, CALLER).status(),
                "The groupName argument is null. Group should not be created.");

        assertEquals(
                Status.ERROR,
                manager.createGroup("createGroupNullArgs", null).status(),
                "The caller argument is null. Group should not be created.");
    }

    @Test
    void addToAbsentGroup() {
        assertEquals(
                Status.ERROR,
                manager.addTo("Absent", "https://google.com/", CALLER).status(),
                "A group with name \"Absent\" do not exist.");

    }

    @Test
    void addToWithInvalidUrl() throws IOException {
        when(parser.parse("www.asd.com")).thenThrow(new IOException());

        assertEquals(
                Status.OK,
                manager.createGroup("addToWithInvalidUrl", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.ERROR,
                manager.addTo("addToWithInvalidUrl", "www.asd.com", CALLER).status(),
                "The provided url is invalid - it should not be added to the group.");
    }

    @Test
    void addToDuplicateBookmark() throws IOException {
        Document document = mock(Document.class);
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://google.com/")).thenReturn(document);

        assertEquals(
                Status.OK,
                manager.createGroup("addToDuplicateBookmark", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.OK,
                manager.addTo("addToDuplicateBookmark", "https://google.com/", CALLER).status(),
                "The provided arguments are valid. Bookmark should be created and added to addToDuplicateBookmark");

        assertEquals(
                Status.ERROR,
                manager.addTo("addToDuplicateBookmark", "https://google.com/", CALLER).status(),
                "This bookmark already exists in the group named addToDuplicateBookmark.");
    }


    @Test
    void addTo() throws IOException {
        Document document = mock(Document.class);
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://google.com/")).thenReturn(document);

        assertEquals(
                Status.OK,
                manager.createGroup("addTo", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.OK,
                manager.addTo("addTo", "https://google.com/", CALLER).status(),
                "The provided arguments are valid. Bookmark should be created and added to addTo");
    }

    @Test
    void addToWithNullArgs() {
        assertEquals(
                Status.ERROR,
                manager.addTo(null, "https://google.com/", CALLER).status(),
                "The groupName argument is null.");

        assertEquals(
                Status.ERROR,
                manager.addTo("groupName", null, CALLER).status(),
                "The url argument is null.");

        assertEquals(
                Status.ERROR,
                manager.addTo("groupName", "https://google.com/", null).status(),
                "The caller argument is null.");
    }


    @Test
    void addToShortenAbsentGroup() {
        assertEquals(
                Status.ERROR,
                manager.addToShorten("Absent", "https://google.com/", CALLER).status(),
                "A group with name \"Absent\" do not exist.");

    }

    @Test
    void addToShortenWithInvalidUrl() throws UrlShortenerException {
        when(shortener.shorten("www.asd.com")).thenThrow(new UrlShortenerException());

        assertEquals(
                Status.OK,
                manager.createGroup("addToShorten", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.ERROR,
                manager.addToShorten("addToShorten", "www.asd.com", CALLER).status(),
                "The provided url is invalid - it should not be added to the group.");
    }

    @Test
    void addToShortenDuplicateBookmark() throws Exception {
        Document document = mock(Document.class);
        when(shortener.shorten("https://google.com/")).thenReturn("https://g.gl/");
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://g.gl/")).thenReturn(document);

        assertEquals(
                Status.OK,
                manager.createGroup("addToShortenDuplicateBookmark", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.OK,
                manager.addToShorten("addToShortenDuplicateBookmark", "https://google.com/", CALLER).status(),
                "The provided arguments are valid. Bookmark should be created and added to addToDuplicateBookmark");

        assertEquals(
                Status.ERROR,
                manager.addToShorten("addToShortenDuplicateBookmark", "https://google.com/", CALLER).status(),
                "This bookmark already exists in the group named addToDuplicateBookmark.");
    }

    @Test
    void addToShortenWithNullArgs() {
        assertEquals(
                Status.ERROR,
                manager.addToShorten(null, "https://google.com/", CALLER).status(),
                "The groupName argument is null.");

        assertEquals(
                Status.ERROR,
                manager.addToShorten("groupName", null, CALLER).status(),
                "The url argument is null.");

        assertEquals(
                Status.ERROR,
                manager.addToShorten("groupName", "https://google.com/", null).status(),
                "The caller argument is null.");
    }

    @Test
    void removeFromAbsentGroup() {
        assertEquals(
                Status.ERROR,
                manager.removeFrom(
                        "removeFromAbsentGroup",
                        "https://google.com/",
                        CALLER).status(),
                "The group is not available. Cannot perform the removal."
        );
    }

    @Test
    void removeFromGroup() throws IOException {
        Document document = mock(Document.class);
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://google.com/")).thenReturn(document);

        assertEquals(
                Status.OK,
                manager.createGroup("removeFromGroup", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.OK,
                manager.addTo("removeFromGroup", "https://google.com/", CALLER).status(),
                "The provided arguments are valid. Bookmark should be created and added to addTo.");

        assertEquals(
                Status.OK,
                manager.removeFrom(
                        "removeFromGroup",
                        "https://google.com/",
                        CALLER).status(),
                "The provided arguments are valid. Bookmark should be removed from \"removeFromGroup\"."
        );
    }

    @Test
    void removeFromGroupInvalidUrl() throws IOException {
        Document document = mock(Document.class);
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://google.com/")).thenReturn(document);

        assertEquals(
                Status.OK,
                manager.createGroup("removeFromGroupInvalidUrl", CALLER).status(),
                "Group should be successfully created. Arguments are valid.");

        assertEquals(
                Status.OK,
                manager.addTo("removeFromGroupInvalidUrl", "https://google.com/", CALLER).status(),
                "The provided arguments are valid. Bookmark should be created and added to addTo.");

        assertEquals(
                Status.ERROR,
                manager.removeFrom(
                        "removeFromGroupInvalidUrl",
                        "https://invalid.com/",
                        CALLER).status(),
                "The provided url is invalid."
        );
    }

    @Test
    void removeFromWithNullArgs() {
        assertEquals(
                Status.ERROR,
                manager.removeFrom(null, "https://google.com/", CALLER).status(),
                "The groupName argument is null. Removal denied.");

        assertEquals(
                Status.ERROR,
                manager.removeFrom("groupName", null, CALLER).status(),
                "The url argument is null. Removal denied.");

        assertEquals(
                Status.ERROR,
                manager.removeFrom("groupName", "https://google.com/", null).status(),
                "The caller argument is null. Removal denied.");
    }

    @Test
    void searchByTagsWithNullArgs() {
        assertEquals(
                Status.ERROR,
                manager.searchByTags(null, CALLER).status(),
                "The tags container is null. Cannot perform search by tags.");

        assertEquals(
                Status.ERROR,
                manager.searchByTags(Collections.emptyList(), null).status(),
                "The caller argument is null. Cannot perform search by tags.");
    }

    @Test
    void searchByTags() throws IOException {
        Document doc1 = mock(Document.class);
        when(doc1.body()).thenReturn(new Element("<body>").text("tag1 tag2"));
        when(doc1.title()).thenReturn("google");
        when(parser.parse("https://google.com/")).thenReturn(doc1);

        Document doc2 = mock(Document.class);
        when(doc2.body()).thenReturn(new Element("<body>").text("tag1"));
        when(doc2.title()).thenReturn("yahoo");
        when(parser.parse("https://yahoo.com/")).thenReturn(doc2);

        manager.createGroup("searchByTags", CALLER);
        manager.addTo("searchByTags", "https://google.com/", CALLER);
        manager.addTo("searchByTags", "https://yahoo.com/", CALLER);

        String expected1 = """
                           ------------------------------
                           [tag1]:\s

                           TITLE: yahoo
                           LINK: https://yahoo.com/
                           TAGS: [tag1, ...]

                           TITLE: google
                           LINK: https://google.com/
                           TAGS: [tag1, tag2, ...]
                           ------------------------------
                           """;

        String expected2 = """
                           ------------------------------
                           [tag2]:\s
                                                      
                           TITLE: google
                           LINK: https://google.com/
                           TAGS: [tag1, tag2, ...]
                           ------------------------------
                           """;


        assertEquals(expected1, manager.searchByTags(List.of("tag1"), CALLER).response());
        assertEquals(expected2, manager.searchByTags(List.of("tag2"), CALLER).response());
    }

    @Test
    void searchByTitle() throws IOException {
        Document doc1 = mock(Document.class);
        when(doc1.body()).thenReturn(new Element("<body>"));
        when(doc1.title()).thenReturn("google");
        when(parser.parse("https://google.com/")).thenReturn(doc1);

        Document doc2 = mock(Document.class);
        when(doc2.body()).thenReturn(new Element("<body>"));
        when(doc2.title()).thenReturn("yahoo");
        when(parser.parse("https://yahoo.com/")).thenReturn(doc2);

        manager.createGroup("searchByTitle", CALLER);
        manager.addTo("searchByTitle", "https://google.com/", CALLER);
        manager.addTo("searchByTitle", "https://yahoo.com/", CALLER);

        String expected1 = """
                           ------------------------------
                           google:\s

                           TITLE: google
                           LINK: https://google.com/
                           TAGS: [, ...]
                           ------------------------------
                           """;

        String expected2 = """
                           ------------------------------
                           yahoo:\s

                           TITLE: yahoo
                           LINK: https://yahoo.com/
                           TAGS: [, ...]
                           ------------------------------
                           """;

        assertEquals(expected1, manager.searchByTitle("google", CALLER).response());
        assertEquals(expected2, manager.searchByTitle("yahoo", CALLER).response());
    }

    @Test
    void searchByTitleWithNullArgs() {
        assertEquals(
                Status.ERROR,
                manager.searchByTitle(null, CALLER).status(),
                "The title argument is null. Cannot perform search by title.");

        assertEquals(
                Status.ERROR,
                manager.searchByTitle("Title", null).status(),
                "The caller argument is null. Cannot perform search by title.");
    }

    @Test
    void searchByTitleWithEmptyTitle() {
        assertEquals(
                Status.ERROR,
                manager.searchByTitle("", CALLER).status(),
                "The title argument is empty. Cannot perform search by title.");
    }

    @Test
    void list() throws IOException {
        String uniqueCaller = "listNoBookmarks";

        Document doc1 = mock(Document.class);
        when(doc1.body()).thenReturn(new Element("<body>").text("tag1 tag2"));
        when(doc1.title()).thenReturn("google");
        when(parser.parse("https://google.com/")).thenReturn(doc1);

        manager.createGroup("listNoBookmarks", uniqueCaller);
        manager.addTo("listNoBookmarks", "https://google.com/", uniqueCaller);

        String expected = """
                          ------------------------------
                          listNoBookmarks:\s
                                                    
                          TITLE: google
                          LINK: https://google.com/
                          TAGS: [tag1, tag2, ...]
                          ------------------------------
                          """;

        assertEquals(expected, manager.list(uniqueCaller).response(), "The list method should print the group name and its content.");
    }

    @Test
    void listByGroup() throws IOException {
        String uniqueCaller = "listNoBookmarks";

        Document doc1 = mock(Document.class);
        when(doc1.body()).thenReturn(new Element("<body>").text("tag1 tag2"));
        when(doc1.title()).thenReturn("google");
        when(parser.parse("https://google.com/")).thenReturn(doc1);

        manager.createGroup("listByGroup", uniqueCaller);
        manager.addTo("listByGroup", "https://google.com/", uniqueCaller);

        String expected = """
                          listByGroup:\s
                                                    
                          TITLE: google
                          LINK: https://google.com/
                          TAGS: [tag1, tag2, ...]
                          ------------------------------
                          """;

        assertEquals(expected, manager.list("listByGroup", uniqueCaller).response(),
                "The list method should print the group name and its content.");
    }

    @Test
    void listByAbsentGroup() {
        assertEquals(Status.ERROR, manager.list("listByAbsentGroup", CALLER).status(),
                "The group do not exists - nothing to print.");
    }

    @Test
    void cleanUpWithAbsentCaller() {
        assertEquals(Status.OK, manager.cleanup("Absent").status(),
                "Caller Absent hooked and cleanup completed.");
        assertEquals("Cleanup completed. Totally removed invalid bookmarks: 0",
                manager.cleanup("Absent").response(),
                "Nothing should be removed.");
    }


    @Test
    void cleanUp() throws IOException, InterruptedException {
        Document document = mock(Document.class);
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://github.com/asdadaddte")).thenReturn(document);
        when(client.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        manager.createGroup("cleanUp", CALLER);
        manager.addTo("cleanUp", "https://github.com/asdadaddte", CALLER);

        assertEquals("Cleanup completed. Totally removed invalid bookmarks: 1",
                manager.cleanup(CALLER).response(),
                "Cleanup should be performed successfully.");
    }

    @Test
    void cleanUpWithRequestFailure() throws IOException, InterruptedException {
        Document document = mock(Document.class);
        when(document.body()).thenReturn(new Element("<body>"));
        when(parser.parse("https://github.com/asdadaddte")).thenReturn(document);
        when(client.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(IOException.class);
        when(responseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        manager.createGroup("cleanUp", CALLER);
        manager.addTo("cleanUp", "https://github.com/asdadaddte", CALLER);

        assertEquals(Status.ERROR, manager.cleanup(CALLER).status(), "Exception is thrown. Cleanup error.");
    }
}
