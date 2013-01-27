package nl.miraclethings.platformer;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

	final static float MAX_VELOCITY = 7f;

	private Body body;
	private Fixture physicsFixture;
	private Fixture sensorFixture;
	private World world;
	private boolean jump;

	MovingPlatform groundedPlatform = null;
	float stillTime = 0;
	long lastGroundTime = 0;

	public Player(World world) {
		this.world = world;

		createPlayer();

		body.setTransform(10.0f, 4.0f, 0);
		body.setFixedRotation(true);
	}

	private void createPlayer() {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		body = world.createBody(def);

		PolygonShape poly = new PolygonShape();
		poly.setAsBox(0.45f, 1.4f);
		physicsFixture = body.createFixture(poly, 1);
		poly.dispose();

		CircleShape circle = new CircleShape();
		circle.setRadius(0.45f);
		circle.setPosition(new Vector2(0, -1.4f));
		sensorFixture = body.createFixture(circle, 0);
		circle.dispose();

		body.setBullet(true);
	}

	public Body getBody() {
		return body;
	}

	public void update() {
		Vector2 vel = body.getLinearVelocity();
		Vector2 pos = body.getPosition();
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
		if (grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if (System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}

		// cap max velocity on x
		if (Math.abs(vel.x) > MAX_VELOCITY) {
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			body.setLinearVelocity(vel.x, vel.y);
		}

		// calculate stilltime & damp
		if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
			stillTime += Gdx.graphics.getDeltaTime();
			body.setLinearVelocity(vel.x * 0.9f, vel.y);
		} else {
			stillTime = 0;
		}

		// disable friction while jumping
		if (!grounded) {
			physicsFixture.setFriction(0f);
			sensorFixture.setFriction(0f);
		} else {
			if (!Gdx.input.isKeyPressed(Keys.A)
					&& !Gdx.input.isKeyPressed(Keys.D) && stillTime > 0.2) {
				physicsFixture.setFriction(10f);
				sensorFixture.setFriction(10f);
				if (groundedPlatform != null) {
					// keep the player moving along with the platform
					body.setLinearVelocity(groundedPlatform.dir.x,
							groundedPlatform.dir.y);
				}
			} else {
				physicsFixture.setFriction(0.2f);
				sensorFixture.setFriction(0.2f);
			}
		}

		// apply left impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			body.applyLinearImpulse(-2f, 0, pos.x, pos.y);
		}

		// apply right impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			body.applyLinearImpulse(2f, 0, pos.x, pos.y);
		}

		// jump, but only when grounded
		if (jump) {
			jump = false;
			if (grounded) {
				body.setLinearVelocity(vel.x, 0);
				System.out.println("jump before: " + body.getLinearVelocity());
				body.setTransform(pos.x, pos.y + 0.01f, 0);
				body.applyLinearImpulse(0, 30, pos.x, pos.y);
				System.out.println("jump, " + body.getLinearVelocity());
			}
		}

		body.setAwake(true);
	}

	private boolean isPlayerGrounded(float deltaTime) {
		groundedPlatform = null;
		List<Contact> contactList = world.getContactList();
		for (int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if (contact.isTouching()
					&& (contact.getFixtureA() == sensorFixture || contact
							.getFixtureB() == sensorFixture)) {

				Vector2 pos = body.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
				}

				if (below) {
					if (contact.getFixtureA().getUserData() != null
							&& contact.getFixtureA().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform) contact
								.getFixtureA().getBody().getUserData();
					}

					if (contact.getFixtureB().getUserData() != null
							&& contact.getFixtureB().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform) contact
								.getFixtureB().getBody().getUserData();
					}
					return true;
				}

				return false;
			}
		}
		return false;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public boolean keyDown(int keycode) {
		if (keycode == Keys.W)
			jump = true;
		return false;
	}

	public boolean keyUp(int keycode) {
		if (keycode == Keys.W)
			jump = false;
		return false;
	}

}
