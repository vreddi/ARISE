<?php
/*
FILE-NAME: connection.php
DESCRIPTION: Establishes a connection with the MYSQL Server and also has the functions
to clean the connection i.e remove the connection and delete all vulnerable private
data.
*/

// To log information
//require('logger.php');

$link = false;
ini_set('display_errors', 'On');
error_reporting(E_ALL | E_STRICT);


    /*
    * Establishes Connection to the MYSQL Server. This function automatically also selects
    * the working database for SplitRide namely 'splitrid_db'.
    *
    * @return connection link
    */
    function getConnection()
    {
        global $link;

        // Connection already exists
        if( $link )
            return $link;

        /* Getting Connection Link */
        /* Implementation: mysql_connect( __HOSTNAME__, __USERNAME__, __PASSWORD__, _Database_) */
        $link = mysqli_connect( '_HOSTNAME_', '_USERNAME_', '_PASSWORD_', '_Database_') or die('Could not connect to server.' );

        // Return the connection link to the requester
        return $link;
    }



    function clearConnection()
    {
        global $link;
        if( $link != false )
            mysqli_close($link);
        $link = false;
    }

?>
