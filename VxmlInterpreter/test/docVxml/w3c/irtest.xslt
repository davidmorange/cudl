<?xml version="1.0"?> 
<!-- Copyright 1998-2003 W3C (MIT, ERCIM, Keio), All Rights Reserved. See http://www.w3.org/Consortium/Legal/. -->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:conf="http://www.w3.org/2002/vxml-conformance"
    xmlns="http://www.w3.org/2001/vxml"
    version="1.0">

<xsl:output cdata-section-elements="script"/>

<!-- ############################################# -->
<!-- D o c u m e n t   H e a d e r s               -->
<!-- ############################################# -->

<!-- Copy everything that doesn't match other rules -->
<xsl:template match="/ | @* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

<!-- strip comments -->
<xsl:template match="comment()"/>

<!-- ############################################# -->
<!-- F i n a l   S t a t u s   I n d i c a t o r s -->
<!-- ############################################# -->

<!-- Success criteria -->
<xsl:template match="conf:pass">
  <prompt>pass</prompt>
  <exit/>
</xsl:template>

<!-- Failure criteria -->
<xsl:template match="conf:fail">
  <prompt>fail</prompt>
  <!-- the following only comes up in case of failure -->
  <xsl:if test="@reason != ''">
    <log>failure reason: <xsl:value-of select="@reason"/></log>
    <prompt><xsl:value-of select="@reason"/></prompt>
  </xsl:if>
  <xsl:if test="@expr != ''">
    <log>failure expression: <value expr="{@expr}"/></log>
    <prompt><value expr="{@expr}"/></prompt>
  </xsl:if>
  <exit/>
</xsl:template>

<!-- ############################################# -->
<!-- I n t e r m e d i a t e   R e p o r t s       -->
<!-- ############################################# -->

<!-- Copy everything doesn't match the other rules -->
<xsl:template match="conf:comment">
  <log>
    <xsl:apply-templates />
  </log>
</xsl:template>

<!-- ############################################# -->
<!-- I n p u t   I n d i c a t o r s               -->
<!-- ############################################# -->

<xsl:template match="conf:hangup">
  <prompt> Hang up now. </prompt>
</xsl:template>

<!-- Recite a recording that DOES contain the specified speech command (alpha, bravo, etc) -->
<xsl:template match="conf:recording[@value]">
  <prompt count="1"> 
    Recite a sentence containing the word 
    '<xsl:call-template name="emit-name-from-token">
        <xsl:with-param name="token" select="@value"/>
      </xsl:call-template>'.
  </prompt>
  <prompt count="2"> 
    Recite a sentence containing the word 
    '<xsl:call-template name="emit-name-from-token">
        <xsl:with-param name="token" select="@value"/>
      </xsl:call-template>' again.
  </prompt>
  <prompt count="3"> 
    Recite a sentence containing the word 
    '<xsl:call-template name="emit-name-from-token">
        <xsl:with-param name="token" select="@value"/>
      </xsl:call-template>' one more time.
  </prompt>
</xsl:template>

<!-- Recite a recording at least 5 seconds in length that 
  does NOT contain a well-defined speech command (alpha, bravo, etc) -->
<!-- Little Miss Muffett sat on her tuffett, eating her curds and whey. 
  Along came a spider ... -->
<!-- Jack and Jill went up the hill to fetch a pail of water. 
  Jack fell down and broke his crown ... -->
<xsl:template match="conf:recording[@value='nonspeech']">
  <prompt count="1"> 
    Recite your favorite nursery rhyme, for example, 'Little Miss Muffett'.
  </prompt>
  <prompt count="2"> Recite your favorite nursery rhyme again.</prompt>
  <prompt count="3"> Recite your favorite nursery rhyme one more time.</prompt>
</xsl:template>

<xsl:template match="conf:noinput">
  <prompt count="1"> No input expected. Say nothing 
    <xsl:if test="@duration"> for <xsl:value-of select="@duration"/> seconds</xsl:if>.
  </prompt>
  <prompt count="2"> No input expected. Say nothing again 
    <xsl:if test="@duration"> for <xsl:value-of select="@duration"/> seconds</xsl:if>.
  </prompt>
  <prompt count="3"> No input expected. Say nothing one more time 
    <xsl:if test="@duration"> for <xsl:value-of select="@duration"/> seconds</xsl:if>.
  </prompt>
</xsl:template>

<xsl:template match="conf:nomatch">
  <prompt count="1"> Say something unrecognizable 
    <xsl:if test="@duration"> for <xsl:value-of select="@duration"/> seconds</xsl:if>.
  </prompt>
  <prompt count="2"> Say something unrecognizable again 
    <xsl:if test="@duration"> for <xsl:value-of select="@duration"/> seconds</xsl:if>.
  </prompt>
  <prompt count="3"> Say something unrecognizable one more time 
    <xsl:if test="@duration"> for <xsl:value-of select="@duration"/> seconds</xsl:if>.
  </prompt>
</xsl:template>

<xsl:template match="*[name()='nomatch']/conf:speech[@value]" priority="2">
   <xsl:call-template name="emit-prompt">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template match="*[name()='noinput']/conf:speech[@value]" priority="2">
   <xsl:call-template name="emit-prompt">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template match="*[name()='block']/conf:speech[@value]" priority="2">
   <xsl:call-template name="emit-prompt">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template match="conf:speech[@value]">
   <xsl:call-template name="emit-prompt">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
     <xsl:with-param name="taper">1</xsl:with-param>     
   </xsl:call-template>
</xsl:template>

<xsl:template match="*[name()='nomatch']/conf:dtmf[@value]" priority="2">
   <xsl:call-template name="emit-dtmf">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
     <xsl:with-param name="taper">0</xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template match="*[name()='noinput']/conf:dtmf[@value]" priority="2">
   <xsl:call-template name="emit-dtmf">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
     <xsl:with-param name="taper">0</xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template match="*[name()='block']/conf:dtmf[@value]" priority="2">
   <xsl:call-template name="emit-dtmf">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
     <xsl:with-param name="taper">0</xsl:with-param>
   </xsl:call-template>
</xsl:template>

<xsl:template match="conf:dtmf[@value]">
   <xsl:call-template name="emit-dtmf">
     <xsl:with-param name="value"><xsl:value-of select="@value"/></xsl:with-param>
     <xsl:with-param name="taper">1</xsl:with-param>
   </xsl:call-template>
</xsl:template>

<!-- ############################################# -->
<!-- G r a m m a r s                               -->
<!-- ############################################# -->

<xsl:template match="conf:grammar[@interp and @utterance]" priority="2">
<xsl:variable name="rootname">CityName<xsl:value-of select="generate-id()"/></xsl:variable>
<grammar type="application/srgs+xml" root="{$rootname}" version="1.0">
  <rule id="{$rootname}" scope="public">
  <one-of>
     <item>
       <xsl:call-template name="emit-utterance">
         <xsl:with-param name="utterance"><xsl:value-of select="@utterance"/></xsl:with-param>
       </xsl:call-template>
      <tag>'<xsl:value-of select="@interp"/>'</tag>
    </item>
  </one-of>
  </rule>
</grammar>
</xsl:template>

<!-- an utterance without an explicit interpretation -->
<xsl:template match="conf:grammar[@utterance]" priority="1">
<xsl:variable name="rootname">CityName<xsl:value-of select="generate-id()"/></xsl:variable>
<grammar type="application/srgs+xml" root="{$rootname}" version="1.0">
  <rule id="{$rootname}" scope="public">
  <one-of>
    <item>
      <xsl:call-template name="emit-utterance">
         <xsl:with-param name="utterance"><xsl:value-of select="@utterance"/></xsl:with-param>
      </xsl:call-template>
    </item>
  </one-of>
  </rule>
</grammar>
</xsl:template>

<xsl:template match="conf:grammar[@utterance and descendant::conf:key]" priority="2">
<xsl:variable name="rootname">CityName<xsl:value-of select="generate-id()"/></xsl:variable>
<grammar type="application/srgs+xml" root="{$rootname}" version="1.0">
  <rule id="{$rootname}" scope="public">
  <one-of>
    <item>
      <xsl:call-template name="emit-utterance">
         <xsl:with-param name="utterance"><xsl:value-of select="@utterance"/></xsl:with-param>
      </xsl:call-template>

    <tag>
      <xsl:apply-templates select="conf:key"/>
    </tag>
    </item>
  </one-of>
  </rule>
</grammar>
</xsl:template>


<xsl:template match="conf:key[@value]" priority="2">
  <xsl:param name="path"/>
  <xsl:choose>  
  <xsl:when test="$path = ''">
    <xsl:text>var </xsl:text>
  </xsl:when>
  <xsl:when test="$path != ''">
    <xsl:value-of select="$path"/><xsl:text>.</xsl:text>
  </xsl:when>
 </xsl:choose>
  
  <xsl:value-of select="@name"/>
  <xsl:text>='</xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>'; </xsl:text>
</xsl:template>

<xsl:template match="conf:key" priority="1">
  <xsl:param name="path"/>
 <xsl:choose>
  <xsl:when test="$path = ''">
    <xsl:text>var </xsl:text>
  </xsl:when>
  <xsl:when test="$path != ''">
    <xsl:value-of select="$path"/><xsl:text>.</xsl:text>
  </xsl:when>
</xsl:choose>
  <xsl:value-of select="@name"/><xsl:text>=new Object(); </xsl:text>
  <xsl:apply-templates select="conf:key">
    <xsl:with-param name="path">
      <xsl:if test="$path != ''">
        <xsl:value-of select="$path"/><xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:value-of select="@name"/>
    </xsl:with-param>
  </xsl:apply-templates>
</xsl:template>


<xsl:template match="conf:phrase[@utterance]">
   <xsl:call-template name="emit-name-from-token">
     <xsl:with-param name="token" select="@utterance"/>
   </xsl:call-template>          
</xsl:template>

<!-- ############################################# -->
<!-- H e l p e r  T e m p l a t e s                            -->
<!-- ############################################# -->

<!-- for use in building grammars -->
<xsl:template name="emit-utterance">
<xsl:param name="utterance"/>
  <xsl:choose>
  <xsl:when test="$utterance='alpha'">chicago</xsl:when>
  <xsl:when test="$utterance='bravo'">san francisco</xsl:when>
  <xsl:when test="$utterance='charlie'">new york</xsl:when>
  <xsl:when test="$utterance='delta'">london</xsl:when>
  <xsl:when test="$utterance='echo'">tokyo</xsl:when>
  <xsl:when test="$utterance='foxtrot'">truth or consequences</xsl:when>
  <xsl:when test="$utterance='golf'">hackensack</xsl:when>
  <xsl:when test="$utterance='hotel'">standardsville</xsl:when>
  <xsl:when test="$utterance='help'">help</xsl:when>
  <xsl:when test="$utterance='cancel'">cancel</xsl:when>
  <xsl:when test="$utterance='exit'">exit</xsl:when>
  <xsl:when test="$utterance='yes'">yes</xsl:when>
  </xsl:choose>
</xsl:template>
<!-- Truth or Consequences is a real city in New Mexico, US.  Hackensack
     is located in New Jersey, US very close to the site of the Sept. 2001
     face-to-face.   And finally, yes, there really is a Standardsville.
     It's in Greene County, Virginia, US. -->

<!-- for use in building prompts -->
<xsl:template name="emit-name-from-token">
<xsl:param name="token"/>
  <xsl:choose>
    <xsl:when test="$token = 'alpha'">Chicago</xsl:when>
    <xsl:when test="$token = 'bravo'">San Francisco</xsl:when>
    <xsl:when test="$token = 'charlie'">New York</xsl:when>
    <xsl:when test="$token = 'delta'">London</xsl:when>
    <xsl:when test="$token = 'echo'">Tokyo</xsl:when>
    <xsl:when test="$token = 'foxtrot'">Truth or Consequences</xsl:when>
    <xsl:when test="$token = 'golf'">Hackensack</xsl:when>
    <xsl:when test="$token = 'hotel'">Standardsville</xsl:when>
    <xsl:when test="$token = 'help'">help</xsl:when>
    <xsl:when test="$token = 'cancel'">cancel</xsl:when>
    <xsl:when test="$token = 'exit'">exit</xsl:when>
    <xsl:when test="$token = 'yes'">yes</xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template name="emit-prompt">
  <xsl:param name="value"/>
  <xsl:param name="taper">0</xsl:param>
  
  <xsl:variable name="text_mapping">
    <xsl:call-template name="emit-name-from-token">
      <xsl:with-param name="token" select="$value"/>
    </xsl:call-template>          
  </xsl:variable>
  
  <xsl:choose>
  <xsl:when test="$taper = 1">
    <prompt count="1">
      Say '<xsl:value-of select="$text_mapping"/>'.
    </prompt>
    <prompt count="2"> 
      Say '<xsl:value-of select="$text_mapping"/>' again.      
    </prompt>
    <prompt count="3">       
      Say '<xsl:value-of select="$text_mapping"/>' one more time.
    </prompt>
  </xsl:when>
  <xsl:otherwise>
    <prompt>
      Say '<xsl:value-of select="$text_mapping"/>'.      
    </prompt>
   </xsl:otherwise>
  </xsl:choose>     
</xsl:template>

<xsl:template name="emit-dtmf">
  <xsl:param name="value"/>
  <xsl:param name="taper">0</xsl:param>

  <xsl:choose>
  <xsl:when test="$taper = 1">
    <prompt count="1"> Press '<xsl:value-of select="@value"/>'. </prompt>
    <prompt count="2"> Press '<xsl:value-of select="@value"/>' again. </prompt>
    <prompt count="3"> Press '<xsl:value-of select="@value"/>' one more time. </prompt>
  </xsl:when>
  <xsl:otherwise>
    <prompt> Press '<xsl:value-of select="$value"/>'. </prompt>
  </xsl:otherwise>
  </xsl:choose>

</xsl:template>


</xsl:stylesheet>

