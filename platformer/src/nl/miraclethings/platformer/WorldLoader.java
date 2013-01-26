package nl.miraclethings.platformer;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class WorldLoader {
	
	public Array<Rectangle> rectangles;

	public WorldLoader(String filename) {
		FileHandle handle = Gdx.files.internal(filename);
		
		this.rectangles = new Array<Rectangle>();
		 
		Document doc = null;
		try {
			doc = Jsoup.parse(handle.read(), "utf-8", "", Parser.xmlParser());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
 
		Elements rects = doc.select("rect");
		for (Element rect : rects) {
			Rectangle r = new Rectangle(
					Float.parseFloat(rect.attr("x")),
					Float.parseFloat(rect.attr("y")),
					Float.parseFloat(rect.attr("width")),
					Float.parseFloat(rect.attr("height"))
					);
			r.y = -r.y - r.height; 
			this.rectangles.add(r);
			Gdx.app.log("WorldLoader", "r: " + r.toString());
		}
		
		Gdx.app.log("WorldLoader", "Rectangles loaded: " + this.rectangles.size);
	}
	
	public Array<Rectangle> getRectangles() {
		return rectangles;
	}

}
