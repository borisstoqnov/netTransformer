<!--
  ~ iTransformer is an open source tool able to discover IP networks
  ~ and to perform dynamic data data population into a xml based inventory system.
  ~ Copyright (C) 2010  http://itransformers.net
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cmp="http://xsltsl.org/cmp">
	<xsl:output omit-xml-declaration="yes" indent="yes"/>
	<xsl:param name="file1"/>
	<xsl:param name="file2"/>
	<xsl:param name="ignored_node_keys_file"/>
    <xsl:param name="ignored_edge_keys_file"/>

    <xsl:strip-space elements="*"/>
	<xsl:variable name="fileA" select="document($file1)"/>
	<xsl:variable name="fileB" select="document($file2)"/>
	<xsl:variable name="ignoredNodeKeysFile" select="document($ignored_node_keys_file)"/>
    <xsl:variable name="ignoredEdgeKeysFile" select="document($ignored_edge_keys_file)"/>

    <!--Compares 2 graphml files. If something is present in A is marked with diff A. If something is present in B is marked with diff B.
     If there is a difference in the data key values an additional diff attribute is added with value = value A - value B. The tag itself takes the value of node B-->
	<xsl:template match="/">
		<xsl:variable name="temp">
			<graphml>
				<graph>
					<xsl:for-each select="$fileA/graphml/graph">
						<xsl:copy-of select="desc|@*" copy-namespaces="no"/>
						<xsl:for-each select="key/@id">
							<xsl:variable name="id" select="."/>
							<xsl:choose>
								<xsl:when test="count($fileB/graphml/graph/key/@id[.=$id])>0">
									<key>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="diff">NO</xsl:attribute>
										<xsl:copy-of select="../@*" copy-namespaces="no"/>
									</key>
								</xsl:when>
								<xsl:otherwise>
									<key>
											<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
											<xsl:attribute name="diff">REMOVED</xsl:attribute>
											<xsl:copy-of select="../@*" copy-namespaces="no"/>
   									</key>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</xsl:for-each>
					<!--go over the keys in B-->
					<xsl:for-each select="$fileB/graphml/graph">
						<xsl:for-each select="key/@id">
							<xsl:variable name="id" select="."/>
							<xsl:choose>
								<xsl:when test="count($fileA/graphml/graph/key/@id[.=$id])=0">
									<key>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="diff">ADDED</xsl:attribute>
										<xsl:copy-of select="../@*" copy-namespaces="no"/>
									</key>
								</xsl:when>
								<xsl:otherwise>
				</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>	
					<!--go over the nodes-->
					<xsl:for-each select="$fileA/graphml/graph">
						<xsl:for-each select="node/@id">
							<xsl:variable name="id" select="."/>
							<xsl:choose>
								<xsl:when test="count($fileB/graphml/graph/node/@id[.=$id])>0">
									<node>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="diff">NO</xsl:attribute>
										<!--find keys present in A -->
										<xsl:for-each select="../data/@key">
											<xsl:variable name="keyA" select="."/>
											<xsl:variable name="valueA" select=".."/>
											<xsl:choose>
												<!--keys are the same-->
												<xsl:when test="count($fileB/graphml/graph/node[@id=$id]/data/@key[.=$keyA])>0">
													<xsl:choose>
														<!--data key value pair the same-->
														<xsl:when test="count($fileB/graphml/graph/node[@id=$id]/data/@key[.=$keyA and .. = $valueA])=1">
															<xsl:copy-of select=".." copy-namespaces="no"/>
														</xsl:when>
														<xsl:otherwise>
															<!--data key pair the same but the values are different-->
                                                            <xsl:choose>
                                                                <xsl:when test="count($ignoredNodeKeysFile/ignored-keys/ignored-key/@name[.=$keyA])>0">
                                                                    <xsl:variable name="valueB" select="$fileB/graphml/graph/node[@id=$id]/data/@key[.=$keyA]/.."/>
                                                                    <data>
                                                                        <xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
                                                                        <xsl:attribute name="diff.ignored"><xsl:value-of select="$valueA"/>-<xsl:value-of select="$valueB"/></xsl:attribute>
                                                                        <xsl:value-of select="$valueB"/>
                                                                    </data>
                                                                </xsl:when>
                                                                <xsl:otherwise>
                                                                    <xsl:variable name="valueB" select="$fileB/graphml/graph/node[@id=$id]/data/@key[.=$keyA]/.."/>
                                                                    <data>
                                                                        <xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
                                                                        <xsl:attribute name="diff"><xsl:value-of select="$valueA"/>-<xsl:value-of select="$valueB"/></xsl:attribute>
                                                                        <xsl:value-of select="$valueB"/>
                                                                    </data>
                                                                </xsl:otherwise>
                                                            </xsl:choose>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<!--KeyA is not present in B-->
													<data>
														<xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
														<xsl:attribute name="diff">REMOVED</xsl:attribute>
														<xsl:value-of select="$valueA"/>
													</data>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										<!--find keys present in B-->
										<xsl:for-each select="$fileB/graphml/graph/node/@id[.=$id]">
											<xsl:for-each select="../data/@key">
												<xsl:variable name="keyB" select="."/>
												<xsl:variable name="valueB" select=".."/>
												<xsl:choose>
													<xsl:when test="count($fileA/graphml/graph/node[@id=$id]/data/@key[.=$keyB])=0">
														<data>
															<xsl:attribute name="key"><xsl:value-of select="$keyB"/></xsl:attribute>
															<xsl:attribute name="diff">ADDED</xsl:attribute>
															<xsl:value-of select="$valueB"/>
														</data>
													</xsl:when>
													<xsl:otherwise>
														<!--key is present and value is already compared-->
													</xsl:otherwise>
												</xsl:choose>
											</xsl:for-each>
										</xsl:for-each>
									</node>
								</xsl:when>
								<xsl:otherwise>
									<node>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="diff">REMOVED</xsl:attribute>
										<xsl:copy-of select="../data" copy-namespaces="no"/>
									</node>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>
					<xsl:for-each select="$fileB/graphml/graph">
						<xsl:for-each select="node/@id">
							<xsl:variable name="id" select="."/>
							<xsl:choose>
								<xsl:when test="count($fileA/graphml/graph/node/@id[.=$id])=0">
									<node>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="diff">ADDED</xsl:attribute>
										<xsl:copy-of select="../data" copy-namespaces="no"/>
									</node>
								</xsl:when>
								<xsl:otherwise>
				</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>
					<!--go over the edges-->
					<xsl:for-each select="$fileA/graphml/graph">
						<xsl:for-each select="edge/@id">
							<xsl:variable name="id" select="."/>
							<xsl:variable name="source" select="../@source"/>
							<xsl:variable name="target" select="../@target"/>
							<xsl:choose>
								<xsl:when test="count($fileB/graphml/graph/edge/@id[.=$id])>0">
									<edge>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="source"><xsl:value-of select="$source"/></xsl:attribute>
										<xsl:attribute name="target"><xsl:value-of select="$target"/></xsl:attribute>
										<xsl:attribute name="diff">NO</xsl:attribute>
										<!--find keys present in A -->
										<xsl:for-each select="../data/@key">
											<xsl:variable name="keyA" select="."/>
											<xsl:variable name="valueA" select=".."/>
											<xsl:choose>
												<!--keys are the same-->
												<xsl:when test="count($fileB/graphml/graph/edge[@id=$id]/data/@key[.=$keyA])>0">
													<xsl:choose>
														<!--data key value pair the same-->
														<xsl:when test="count($fileB/graphml/graph/edge[@id=$id]/data/@key[.=$keyA and .. = $valueA])=1">
															<xsl:copy-of select=".." copy-namespaces="no"/>
														</xsl:when>
														<xsl:otherwise>
															<!--data key pair the same but the values are different-->

															<xsl:variable name="valueB" select="$fileB/graphml/graph/edge[@id=$id]/data/@key[.=$keyA]/.."/>
															<data>
																<xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
                                                                <xsl:attribute name="diff"><xsl:value-of select="$valueA"/>-<xsl:value-of select="$valueB"/></xsl:attribute>
																<xsl:value-of select="$valueB"/>
															</data>

                                                            <!--data key pair the same but the values are different-->
                                                            <xsl:choose>
                                                                <xsl:when test="count($ignoredEdgeKeysFile/ignored-keys/ignored-key/@name[.=$keyA])>0">
                                                                    <xsl:variable name="valueB" select="$fileB/graphml/graph/edge[@id=$id]/data/@key[.=$keyA]/.."/>
                                                                    <data>
                                                                        <xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
                                                                        <xsl:attribute name="diff.ignored"><xsl:value-of select="$valueA"/>-<xsl:value-of select="$valueB"/></xsl:attribute>
                                                                        <xsl:value-of select="$valueB"/>
                                                                    </data>
                                                                </xsl:when>
                                                                <xsl:otherwise>
                                                                    <xsl:variable name="valueB" select="$fileB/graphml/graph/node[@id=$id]/data/@key[.=$keyA]/.."/>
                                                                    <data>
                                                                        <xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
                                                                        <xsl:attribute name="diff"><xsl:value-of select="$valueA"/>-<xsl:value-of select="$valueB"/></xsl:attribute>
                                                                        <xsl:value-of select="$valueB"/>
                                                                    </data>
                                                                </xsl:otherwise>
                                                            </xsl:choose>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise>
													<!--KeyA is not present in B-->
													<data>
														<xsl:attribute name="key"><xsl:value-of select="$keyA"/></xsl:attribute>
														<xsl:attribute name="diff">REMOVED</xsl:attribute>
														<xsl:value-of select="$valueA"/>
														<xsl:copy-of select="../data" copy-namespaces="no"/>
													</data>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										<!--find keys present only in B-->
										<xsl:for-each select="$fileB/graphml/graph/edge/@id[.=$id]">
											<xsl:for-each select="../data/@key">
												<xsl:variable name="keyB" select="."/>
												<xsl:variable name="valueB" select=".."/>
												<xsl:choose>
													<xsl:when test="count($fileA/graphml/graph/edge[@id=$id]/data/@key[.=$keyB])=0">
														<data>
															<xsl:attribute name="key"><xsl:value-of select="$keyB"/></xsl:attribute>
															<xsl:attribute name="diff">ADDED</xsl:attribute>
															<xsl:value-of select="$valueB"/>
														</data>
													</xsl:when>
													<xsl:otherwise>
														<!--key is present and value is already compared-->
													</xsl:otherwise>
												</xsl:choose>
											</xsl:for-each>
										</xsl:for-each>
									</edge>
								</xsl:when>
								<xsl:otherwise>
								<!--edge is present only in A-->
									<edge>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="source"><xsl:value-of select="$source"/></xsl:attribute>
										<xsl:attribute name="target"><xsl:value-of select="$target"/></xsl:attribute>
										<xsl:attribute name="diff">REMOVED</xsl:attribute>
										<xsl:copy-of select="../data" copy-namespaces="no"/>
									</edge>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>
					<!--Find edges present only in B-->
					<xsl:for-each select="$fileB/graphml/graph">
						<xsl:for-each select="edge/@id">
							<xsl:variable name="id" select="."/>
							<xsl:variable name="source" select="../@source"/>
							<xsl:variable name="target" select="../@target"/>
							<xsl:choose>
								<xsl:when test="count($fileA/graphml/graph/edge/@id[.=$id])=0">
									<edge>
										<xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
										<xsl:attribute name="diff">ADDED</xsl:attribute>
										<xsl:attribute name="source"><xsl:value-of select="$source"/></xsl:attribute>
										<xsl:attribute name="target"><xsl:value-of select="$target"/></xsl:attribute>
										<xsl:copy-of select="../data" copy-namespaces="no"/>
									</edge>
								</xsl:when>
								<xsl:otherwise>
				</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>
				</graph>
			</graphml>
		</xsl:variable>

        <!-- Dump temporary xml -->
        <!--<xsl:message><xsl:copy-of select="$temp"/> </xsl:message>-->

		<graphml>
			<graph>
				<xsl:copy-of select="$temp/graphml/graph/@*" copy-namespaces="no"/>
				<xsl:copy-of select="$temp/graphml/graph/key" copy-namespaces="no"/>
				<xsl:for-each select="$temp/graphml/graph/node">
					<xsl:variable name="node" select="."/>
					<xsl:variable name="diff" select="@diff"/>
					<node>
						<xsl:attribute name="id" select="@id"/>
						<!--copy all the data keys that have no diff-->
						<xsl:for-each select="data[@key and not(@diff or @diff.ignored)]">
							<xsl:copy-of select="." copy-namespaces="no"/>
						</xsl:for-each>
						<!--for those that have diff-->
						<xsl:choose>
							<!--for those that are added or are removed-->
							<xsl:when test="$diff='ADDED' or $diff='REMOVED'">
								<data>
									<xsl:attribute name="key">diff</xsl:attribute>
									<xsl:value-of select="$diff"/>
								</data>
								<data><xsl:attribute name="key">diffs</xsl:attribute>Node: <xsl:value-of select="$diff"/></data>	
							</xsl:when>
							<!-- for those that has different values-->
							<xsl:otherwise>
                                <!-- determine type of the diff for current node. Default value is 'NO'-->
                                <xsl:variable name="diffType">
                                    <xsl:choose>
                                        <xsl:when test="count(data[@key and @diff])>0">
                                            <xsl:text>YES</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:choose>
                                                <xsl:when test="count(data[@key and @diff.ignored])>0">
                                                    <xsl:text>IGNORED</xsl:text>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:text>NO</xsl:text>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <xsl:choose>
                                    <xsl:when test="$diffType='NO'">
                                        <xsl:variable name="diffs">
                                            <data>
                                                <xsl:attribute name="key">diffs</xsl:attribute>
                                                <!--xsl:value-of select="data[@key='nodeInfo']"/-->
                                                <xsl:for-each select="data[@key and @diff]">&lt;br&gt;&lt;b&gt;&lt;i&gt;<xsl:value-of select="@key"/>
                                                    <xsl:text>: </xsl:text>&lt;/i&gt;&lt;/b&gt;<xsl:value-of select="@diff"/>
                                                    <xsl:text> - </xsl:text>
                                                    <xsl:value-of select="."/>&lt;/br&gt;</xsl:for-each>
                                            </data>
                                        </xsl:variable>
                                        <xsl:copy-of select="$diffs"/>
                                        <!-- no diffs -->
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <!--<xsl:message><xsl:text>DiffType:</xsl:text><xsl:value-of select="$diffType"/> </xsl:message>-->
                                        <xsl:for-each select="data[@key and (@diff or @diff.ignored)]">
                                            <data>
                                                <xsl:attribute name="key"><xsl:value-of select="@key"/></xsl:attribute>
                                                <xsl:value-of select="."/>
                                            </data>
                                        </xsl:for-each>
                                        <data><xsl:attribute name="key">diff</xsl:attribute><xsl:value-of select="$diffType"/></data>
                                        <xsl:variable name="diffs">
                                            <data>
                                                <xsl:attribute name="key">diffs</xsl:attribute>
                                                <!--xsl:value-of select="data[@key='nodeInfo']"/-->
                                                <xsl:for-each select="data[@key and @diff]">&lt;br&gt;&lt;b&gt;&lt;i&gt;<xsl:value-of select="@key"/>
                                                    <xsl:text>: </xsl:text>&lt;/i&gt;&lt;/b&gt;<xsl:value-of select="@diff"/>
                                                    <xsl:text> - </xsl:text>
                                                    <xsl:value-of select="."/>&lt;/br&gt;</xsl:for-each>
                                            </data>
                                        </xsl:variable>
                                        <xsl:copy-of select="$diffs"/>
                                    </xsl:otherwise>
                                </xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</node>
				</xsl:for-each>
				
				<xsl:for-each select="$temp/graphml/graph/edge">
					<xsl:variable name="edge" select="."/>
					<xsl:variable name="diff" select="@diff"/>
					<edge>
						<xsl:attribute name="id" select="@id"/>
						<xsl:copy-of select="@source"/>
						<xsl:copy-of select="@target"/>
						<!--copy all the data keys that have no diff-->
						<xsl:for-each select="data[@key and not(@diff)]">
							<xsl:copy-of select="." copy-namespaces="no"/>
						</xsl:for-each>
						<!--for those that are added or removedf-->
						<xsl:choose>
							<xsl:when test="$diff='ADDED' or $diff='REMOVED'">
								<data>
									<xsl:attribute name="key">diff</xsl:attribute>
									<xsl:value-of select="$diff"/>
								</data>
								<data><xsl:attribute name="key">diffs</xsl:attribute><!--<xsl:value-of select="data[@key='edgeTooltip']/>--> Edge: <xsl:value-of select="$diff"/></data>
							</xsl:when>
							<!--for those that has difference-->
							<xsl:otherwise>
                                <xsl:variable name="diffType">
                                    <xsl:choose>
                                        <xsl:when test="count(data[@key and @diff])>0">
                                            <xsl:text>YES</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:choose>
                                                <xsl:when test="count(data[@key and @diff.ignored])>0">
                                                    <xsl:text>IGNORED</xsl:text>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:text>NO</xsl:text>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

								<xsl:choose>
									<xsl:when test="$diffType='NO'">
										<data><xsl:attribute name="key">diff</xsl:attribute>YES</data>
										<xsl:for-each select="data[@key and @diff]">
											<data>
												<xsl:attribute name="key"><xsl:value-of select="@key"/></xsl:attribute>
												<xsl:value-of select="."/>
											</data>
										</xsl:for-each>
										<xsl:variable name="diffs">
											<data>
												<xsl:attribute name="key">diffs</xsl:attribute>
                                                <!--xsl:value-of select="data[@key='edgeTooltip']"/-->
												<xsl:for-each select="data[@key and @diff]">&lt;br&gt;&lt;b&gt;&lt;i&gt;<xsl:value-of select="@key"/>
													<xsl:text>: </xsl:text>&lt;/i&gt;&lt;/b&gt;<xsl:value-of select="@diff"/>
													<xsl:text> - </xsl:text>
													<xsl:value-of select="."/>&lt;/br&gt;</xsl:for-each>
											</data>
										</xsl:variable>
										<xsl:copy-of select="$diffs"/>
									</xsl:when>
									<xsl:otherwise>
                                        <xsl:for-each select="data[@key and (@diff or @diff.ignored)]">
                                            <data>
                                                <xsl:attribute name="key"><xsl:value-of select="@key"/></xsl:attribute>
                                                <xsl:value-of select="."/>
                                            </data>
                                        </xsl:for-each>
                                        <data><xsl:attribute name="key">diff</xsl:attribute><xsl:value-of select="$diffType"/></data>
                                        <xsl:variable name="diffs">
                                            <data>
                                                <xsl:attribute name="key">diffs</xsl:attribute>
                                                <!--xsl:value-of select="data[@key='nodeInfo']"/-->
                                                <xsl:for-each select="data[@key and @diff]">&lt;br&gt;&lt;b&gt;&lt;i&gt;<xsl:value-of select="@key"/>
                                                    <xsl:text>: </xsl:text>&lt;/i&gt;&lt;/b&gt;<xsl:value-of select="@diff"/>
                                                    <xsl:text> - </xsl:text>
                                                    <xsl:value-of select="."/>&lt;/br&gt;</xsl:for-each>
                                            </data>
                                        </xsl:variable>
                                        <xsl:copy-of select="$diffs"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</edge>
				</xsl:for-each>
			</graph>
		</graphml>
	</xsl:template>
</xsl:stylesheet>
