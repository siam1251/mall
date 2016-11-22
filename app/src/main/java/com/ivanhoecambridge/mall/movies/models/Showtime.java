package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Kay on 2016-11-04.
 */

@Root(strict = false)
public class Showtime {
    @Element(required = false)
    private String showtime;
}
