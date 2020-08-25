package de.marcely.pcel;

import org.bukkit.entity.EntityType;

public enum CameraEffect {
	
	NORMAL(EntityType.VILLAGER),
	CREEPER(EntityType.CREEPER),
	ENDERMAN(EntityType.ENDERMAN),
	SPIDER(EntityType.SPIDER);
	
	private final EntityType type;
	
	CameraEffect(EntityType type){
		this.type = type;
	}
	
	public EntityType getEntityType(){
		return this.type;
	}
	
	public static CameraEffect getByName(String str) {
		for(CameraEffect effect : values()){
			if(effect.name().equalsIgnoreCase(str))
				return effect;
		}
		
		return null;
	}
}
