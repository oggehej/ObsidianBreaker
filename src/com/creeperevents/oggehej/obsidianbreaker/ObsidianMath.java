package com.creeperevents.oggehej.obsidianbreaker;

import java.text.DecimalFormat;

public class ObsidianMath
{
	/**
	 * Round value to two decimals and
	 * remove unnecessary value figures
	 * 
	 * @param num Number
	 * @return New number
	 */
	public static String smartRound(double num)
	{
		DecimalFormat f = new DecimalFormat("##.##");
		return f.format(num).replace(",", ".");
	}
}
