package com.creeperevents.oggehej;

import java.text.DecimalFormat;

public class ObsidianMath
{
	public static String smartRound(double num)
	{
		DecimalFormat f = new DecimalFormat("##.##");
		return f.format(num).replace(",", ".");
	}
}
