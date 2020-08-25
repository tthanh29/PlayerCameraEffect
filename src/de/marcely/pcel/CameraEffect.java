package de.marcely.pcel;

import org.bukkit.entity.EntityType;

public enum CameraEffect {
	
	NORMAL(EntityType.VILLAGER),
	CREEPER(EntityType.CREEPER),
	ENDERMAN(EntityType.ENDERMAN),
	SPIDER(EntityType.SPIDER);
	
	private EntityType selected_type;
	
	private CameraEffect(EntityType type){
		this.selected_type = type;
	}
	
	public EntityType getEntityType(){
		return this.selected_type;
	}
	
	public static CameraEffect getCameraEffect(String str){
		for(CameraEffect effect:values()){
			if(effect.name().equalsIgnoreCase(str))
				return effect;
		}
		
		return null;
	}
}
