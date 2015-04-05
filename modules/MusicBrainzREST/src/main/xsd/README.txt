xjc should support relaxng, but it miserably fails with this file.

Trang has been used to convert to a regular XSD. It the had to be manually fixed patching row 1246:

    <xs:attribute name="offset2" type="xs:nonNegativeInteger"/>


java -jar tmp/trang-20081028/trang.jar -I rng -O xsd src/main/xsd/musicbrainz_mmd-2.0.rng src/main/xsd/musicbrainz_mmd-2.0.xsd