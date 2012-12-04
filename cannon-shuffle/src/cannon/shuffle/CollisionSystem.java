package cannon.shuffle;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import cannon.shuffle.Barrel.BarrelPart;
import cannon.shuffle.Barrel.ShieldPart;

public class CollisionSystem implements ContactListener{

	Object A, B;
	
	public boolean testCollision(String C1, String C2, GameEntity A, GameEntity B){
		if ( ((A.getClass().getSimpleName().equals(C1)) && (B.getClass().getSimpleName().equals(C2))) ){
			this.A = A;
			this.B = B;
			return true;
		}
		else if ((A.getClass().getSimpleName().equals(C2)) && (B.getClass().getSimpleName().equals(C1)) ){
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
		
		if ( testCollision(CannonShuffle.BULLET, CannonShuffle.BULLET, A, B) ){
			Bullet bullet1 = (Bullet) this.A;
			Bullet bullet2 = (Bullet) this.B;
			if ( bullet1.crushableType == bullet2.crushableType ){
				bullet1.destroyed = true;
				bullet2.destroyed = true;
				CannonShuffle.explosions.add(bullet1.getExplosion(true, bullet2.getPosition(), 0.5f));
			}
			else if ( bullet1.crushableType < bullet2.crushableType ){
				bullet1.destroyed = true;
				CannonShuffle.explosions.add(bullet1.getExplosion(false, null, -1));
			}
			else{
				bullet2.destroyed = true;
				CannonShuffle.explosions.add(bullet2.getExplosion(false, null, -1));
			}
		}
		if ( testCollision(CannonShuffle.BULLET, CannonShuffle.BARREL_PART, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			CannonShuffle.explosions.add(bullet.getExplosion(false, null, -1));
			BarrelPart barrel = (BarrelPart) this.B;
			CannonShuffle.cannon.hp -= bullet.damage*(1-barrel.protection);
		}
		if ( testCollision(CannonShuffle.BULLET, CannonShuffle.SHIELD_PART, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			CannonShuffle.explosions.add(bullet.getExplosion(false, null, -1));
			ShieldPart shield = (ShieldPart) this.B;
			CannonShuffle.cannon.hp -= bullet.damage*(1-shield.protection);
		}
		if ( testCollision(CannonShuffle.BULLET, CannonShuffle.CANNON, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			CannonShuffle.explosions.add(bullet.getExplosion(false, null, -1));
			CannonShuffle.cannon.hp -= bullet.damage*(1-CannonShuffle.cannon.protection);
		}
		if ( testCollision(CannonShuffle.BULLET, CannonShuffle.WALL, A, B) ){
			Bullet bullet = (Bullet) this.A;
			bullet.destroyed = true;
			CannonShuffle.explosions.add(bullet.getExplosion(false, null, -1));
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
