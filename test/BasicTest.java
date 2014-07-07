import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
      Fixtures.deleteDatabase();
    }
    @Test
    public void createAndRetrieveUser(){
      new User("bob@gmail.com", "secret", "bob").save();
      User bob = User.find("byEmail", "bob@gmail.com").first();
      assertNotNull(bob);
      assertEquals("bob", bob.fullname);
    }

    @Test
    public void tryConnectAUser() {
      new User("bob@gmail.com", "secret", "bob").save();
      assertNotNull(User.connect("bob@gmail.com", "secret"));
      assertNull(User.connect("bob@gmail.com", "badpassword"));
      assertNull(User.connect("tom@gmail.com", "secret"));
    }

    @Test
    public void createPost() {
      User bob = new User("bob@gmail.com", "secret", "Bob").save();
      new Post(bob, "My first post", "Hello world").save();
      assertEquals(1, Post.count());
      
      List<Post> bobPosts = Post.find("byAuthor", bob).fetch();
      assertEquals(1, bobPosts.size());
      Post firstPost = bobPosts.get(0);
      assertNotNull(firstPost);
      assertEquals(bob, firstPost.author);
      assertEquals("My first post", firstPost.title);
      assertEquals("Hello world", firstPost.content);
      assertNotNull(firstPost.postedAt);
    }
    
    @Test
    public void fullTest() {
      Fixtures.loadModels("data.yml");
   
      // Count things
      assertEquals(2, User.count());
      assertEquals(3, Post.count());
      assertEquals(3, Comment.count());
   
      // Try to connect as users
      assertNotNull(User.connect("bob@gmail.com", "secret"));
      assertNotNull(User.connect("jeff@gmail.com", "secret"));
      assertNull(User.connect("jeff@gmail.com", "badpassword"));
      assertNull(User.connect("tom@gmail.com", "secret"));
   
      // Find all of Bob's posts
      List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
      assertEquals(2, bobPosts.size());
   
      // Find all comments related to Bob's posts
      List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
      assertEquals(3, bobComments.size());
   
      // Find the most recent post
      Post frontPost = Post.find("order by postedAt desc").first();
      assertNotNull(frontPost);
      assertEquals("About the model layer", frontPost.title);
   
      // Check that this post has two comments
      assertEquals(2, frontPost.comments.size());
   
      // Post a new comment
      frontPost.addComment("Jim", "Hello guys");
      assertEquals(3, frontPost.comments.size());
      assertEquals(4, Comment.count());
    }
}
