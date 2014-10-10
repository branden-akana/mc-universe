package com.octopod.switchcore.packets;

import com.octopod.switchcore.ServerProperty;

/**
 * @author Octopod - octopodsquad@gmail.com
 */
public class PacketInServerUpdate extends SwitchPacket
{
	String class_name;

	public PacketInServerUpdate(Class<? extends ServerProperty> type)
	{
		this.class_name = type.getName();
	}

	@SuppressWarnings("unchecked")
	public Class<? extends ServerProperty> getPropertyClass()
	{
		try
		{
			return (Class<? extends ServerProperty>)Class.forName(class_name);
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}

	public String getPropertyClassName()
	{
		return class_name;
	}

	public boolean propertyExists()
	{
		try
		{
			Class.forName(class_name);
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
}
