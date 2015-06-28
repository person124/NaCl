package com.person124.plugin.hoor;

import com.person124.plugin.PersonPlugins;

public class NaCl extends PersonPlugins {
	
	public NaCl() {
		super(true, new TNTArrows(), new ShopDesk(), new GunpowderPlots(), new DeathChest(), new StupidParticles(), new SABD(), new AnnoyOTron());
		hasCommand("person.nacl");
	}

}
