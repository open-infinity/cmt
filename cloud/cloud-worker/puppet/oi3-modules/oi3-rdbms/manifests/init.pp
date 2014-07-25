class oi3-rdbms {

    case $operatingsystem {
            'Ubuntu': {
                include mariadbrepo
            }
    }

    require oi3-basic
    include oi3-rdbms::install, oi3-rdbms::config, oi3-rdbms::service
}

