package cannon.shuffle;

import cannon.shuffle.bullet.Bullet;
import cannon.shuffle.cannon.BarrelPart;
import cannon.shuffle.cannon.ShieldPart;
import cannon.shuffle.enemy.Enemy;
import cannon.shuffle.screen.GameScreen;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionSystem implements ContactListener{

	Object A, B;

	public boolean testCollision(String C1, String C2, GameEntity A, GameEntity B){
		if ( ((A.hasType(C1)) && (B.hasType(C2))) ){
			this.A = A;
			this.B = B;
			return true;
		}
		else if ((A.hasType(C2)) && (B.hasType(C1)) ){
			this.A = B;
			this.B = A;
			return true;
		}
		return false;
	}
	@Override
	public void beginContact(Contact contact) {
		GameEntity A = (GameEntity) contact.getFixtureA().getBody().getUserData();
		GameEntity B = (GameEntity) contact.getFixtureB().getBody().getUserData();

		if ( testCollision(Entities.BULLET, Entities.BULLET, A, B) ){
			Bullet bullet1 = (Bullet) this.A;
			Bullet bullet2 = (Bullet) this.B;
			if ( bullet1.crushableType == bullet2.crushableType ){
				bullet1.destroyed = true;
				bullet2.destroyed = true;
				GameScreen.explosions.add(bullet1.getExplosion(true, bullet2.getPosition(), 0.5f));
			}
			else if ( bullet1.crushableType < bullet2.crushableType ){
				bullet1.destroyed = true;
				GameScreen.explosions.add(bullet1.getExplosion(false, null, -1));
			}
			else{
				bullet2.destroyed = true;
				GameScreen.explosions.add(bullet2.getExplosion(false, null, -1));
			}
		}
		if ( testCollision(Entities.BULLET, Entities.BARREL_PART, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			GameScreen.explosions.add(bullet.getExplosion(false, null, -1));
			BarrelPart barrel = (BarrelPart) this.B;
			GameScreen.cannon.hp -= bullet.damage*(1-barrel.protection);
		}
		if ( testCollision(Entities.BULLET, Entities.SHIELD_PART, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			GameScreen.explosions.add(bullet.getExplosion(false, null, -1));
			ShieldPart shield = (ShieldPart) this.B;
			GameScreen.cannon.hp -= bullet.damage*(1-shield.protection);
		}
		if ( testCollision(Entities.BULLET, Entities.CANNON, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			GameScreen.explosions.add(bullet.getExplosion(false, null, -1));
			GameScreen.cannon.hp -= bullet.damage*(1-GameScreen.cannon.protection);
		}
		if ( testCollision(Entities.BULLET, Entities.WALL, A, B) ){
			Wall wall = (Wall) this.B;
			if ( !wall.is_invisible ){
				Bullet bullet = (Bullet) this.A;
				bullet.destroyed = true;
				GameScreen.explosions.add(bullet.getExplosion(false, null, -1));
			}
		}
		if ( testCollision(Entities.BULLET, Entities.ENEMY, A, B) ){
			Bullet bullet = (Bullet) this.A;
			if ( !bullet.is_enemy_attack ){
				bullet.destroyed = true;
				GameScreen.explosions.add(bullet.getExplosion(false, null, -1));
				Enemy enemy = (Enemy) this.B;
				enemy.hp -= bullet.damage*(1-enemy.protection);
				if ( enemy.hp <= 0 ){
					enemy.destroyed = true;
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
