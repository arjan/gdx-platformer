package nl.miraclethings.platformer;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class GameLevel {
	
	public World world;
	private String levelname;
	
	//public Array<Rectangle> rectangles;

	public GameLevel(String levelname) {
		
		this.world = new World(new Vector2(0, -20), true);		

		this.levelname = levelname;
		
		loadGeometry();
	}
	
	protected void loadGeometry() {
		FileHandle handle = Gdx.files.internal(levelname + ".svg");
		
		Document doc = null;
		try {
			doc = Jsoup.parse(handle.read(), "utf-8", "", Parser.xmlParser());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
 
		float x, y, width, height;
		float scale = 0.02f;
		
		Elements rects = doc.select("rect");
		for (Element rect : rects) {
			x = Float.parseFloat(rect.attr("x"));
			y = Float.parseFloat(rect.attr("y"));
			width = Float.parseFloat(rect.attr("width"));
			height = Float.parseFloat(rect.attr("height"));
			
			y = -y - height;
			
			Body box = createBox(BodyType.StaticBody, 0.5f*width*scale, 0.5f*height*scale, 0f);
			box.setTransform((x+width*0.5f)*scale, (1000+y+height*0.5f)*scale, 0f);
		}

		Gdx.app.log("WorldLoader", "Rectangles loaded: " + rects.size());
	}
	
	private Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
	
	
	public World getWorld() {
		return this.world;
	}

}
