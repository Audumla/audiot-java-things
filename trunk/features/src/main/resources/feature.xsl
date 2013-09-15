<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:karaf="http://karaf.apache.org/xmlns/features/v1.2.0">

    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="karaf:bundle">
        <xsl:choose>
            <xsl:when
                    test="contains(text(),'log4j') or contains(text(),'commons-logging') or contains(text(),'slf4j') or contains(text(),'ftd2xxj') or contains(text(),'osgi')">
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:value-of select="text()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="karaf:features">
        <features xmlns="http://karaf.apache.org/xmlns/features/v1.0.0">
            <xsl:copy-of select="@name"/>
            <xsl:apply-templates select="*"/>
        </features>
    </xsl:template>

    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="text()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>