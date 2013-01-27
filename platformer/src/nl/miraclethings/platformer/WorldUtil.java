package nl.miraclethings.platformer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class WorldUtil {

	public static Body createCircle(World world, BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		CircleShape poly = new CircleShape();
		poly.setRadius(radius);
		box.createFixture(poly, density);
		poly.dispose();

		return box;
	}

	public static Body createBox(World world, BodyType type, float width, float height,
			float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		box.createFixture(poly, density);
		poly.dispose();

		return box;
	}

	public static Body createEdge(World world, BodyType type, float x1, float y1, float x2,
			float y2, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		EdgeShape edge = new EdgeShape();
		edge.set(new Vector2(0, 0), new Vector2(x2 - x1, y2 - y1));
		box.createFixture(edge, density);
		box.setTransform(x1, y1, 0);
		edge.dispose();

		return box;
	}

}
