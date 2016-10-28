package com.ivanhoecambridge.mall.movies.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Kay on 2016-10-27.
 */

@Root(strict = false)
public class Ratings {

    public static final String RATINGS_PROVINCE_CODE_ON = "on-rating";
    public static final String RATINGS_PROVINCE_CODE_BC = "bc-rating";

    @Element(name = "pq-rating", required = false)
    private String pq_rating;

    @Element(name = "ire-rating", required = false)
    private String ire_rating;

    @Element(name = "nb-rating", required = false)
    private String nb_rating;

    @Element(name = "bc-rating", required = false)
    private String bc_rating;

    @Element(name = "on-rating", required = false)
    private String on_rating;

    @Element(name = "ab-rating", required = false)
    private String ab_rating;

    @Element(name = "yt-rating", required = false)
    private String yt_rating;

    @Element(name = "nf-rating", required = false)
    private String nf_rating;

    @Element(name = "ns-rating", required = false)
    private String ns_rating;

    @Element(name = "mb-rating", required = false)
    private String mb_rating;

    @Element(name = "sk-rating", required = false)
    private String sk_rating;

    @Element(name = "pe-rating", required = false)
    private String pe_rating;

    @Element(name = "uk-rating", required = false)
    private String uk_rating;

    public String getPq_rating ()
    {
        return pq_rating;
    }

    public void setPq_rating (String pq_rating)
    {
        this.pq_rating = pq_rating;
    }

    public String getIre_rating ()
    {
        return ire_rating;
    }

    public void setIre_rating (String ire_rating)
    {
        this.ire_rating = ire_rating;
    }

    public String getNb_rating ()
    {
        return nb_rating;
    }

    public void setNb_rating (String nb_rating)
    {
        this.nb_rating = nb_rating;
    }

    public String getBc_rating ()
    {
        return bc_rating;
    }

    public void setBc_rating (String bc_rating)
    {
        this.bc_rating = bc_rating;
    }

    public String getOn_rating ()
    {
        return on_rating;
    }

    public void setOn_rating (String on_rating)
    {
        this.on_rating = on_rating;
    }

    public String getAb_rating ()
    {
        return ab_rating;
    }

    public void setAb_rating (String ab_rating)
    {
        this.ab_rating = ab_rating;
    }

    public String getYt_rating ()
    {
        return yt_rating;
    }

    public void setYt_rating (String yt_rating)
    {
        this.yt_rating = yt_rating;
    }

    public String getNf_rating ()
    {
        return nf_rating;
    }

    public void setNf_rating (String nf_rating)
    {
        this.nf_rating = nf_rating;
    }

    public String getNs_rating ()
    {
        return ns_rating;
    }

    public void setNs_rating (String ns_rating)
    {
        this.ns_rating = ns_rating;
    }

    public String getMb_rating ()
    {
        return mb_rating;
    }

    public void setMb_rating (String mb_rating)
    {
        this.mb_rating = mb_rating;
    }

    public String getSk_rating ()
    {
        return sk_rating;
    }

    public void setSk_rating (String sk_rating)
    {
        this.sk_rating = sk_rating;
    }

    public String getPe_rating ()
    {
        return pe_rating;
    }

    public void setPe_rating (String pe_rating)
    {
        this.pe_rating = pe_rating;
    }

    public String getUk_rating ()
    {
        return uk_rating;
    }

    public void setUk_rating (String uk_rating)
    {
        this.uk_rating = uk_rating;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [pq_rating = "+pq_rating+", ire_rating = "+ire_rating+", nb_rating = "+nb_rating+", bc_rating = "+bc_rating+", on_rating = "+on_rating+", ab_rating = "+ab_rating+", yt_rating = "+yt_rating+", nf_rating = "+nf_rating+", ns_rating = "+ns_rating+", mb_rating = "+mb_rating+", sk_rating = "+sk_rating+", pe_rating = "+pe_rating+", uk_rating = "+uk_rating+"]";
    }
}
